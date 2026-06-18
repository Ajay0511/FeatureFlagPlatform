package com.example.featureflagplatform.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.featureflagplatform.dto.request.AddRuleRequest;
import com.example.featureflagplatform.dto.request.CreateFeatureFlagRequest;
import com.example.featureflagplatform.dto.request.UpdateFeatureFlagStatusRequest;
import com.example.featureflagplatform.dto.request.UpdateRolloutRequest;
import com.example.featureflagplatform.dto.response.FeatureFlagEvaluationResponse;
import com.example.featureflagplatform.dto.response.FeatureFlagResponse;
import com.example.featureflagplatform.dto.response.FeatureFlagStatusResponse;
import com.example.featureflagplatform.dto.response.RuleResponse;
import com.example.featureflagplatform.entity.FeatureFlag;
import com.example.featureflagplatform.entity.FeatureFlagRule;
import com.example.featureflagplatform.exception.FlagAlreadyExistsException;
import com.example.featureflagplatform.exception.FlagNotFoundCustomException;
import com.example.featureflagplatform.repository.FeatureFlagRepository;
import com.example.featureflagplatform.repository.FeatureFlagRuleRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
@Transactional
public class FeatureFlagServiceImpl implements FeatureFlagService {
 
    private final FeatureFlagRepository featureFlagRepository;

    private final FeatureFlagRuleRepository featureFlagRuleRepository;

    @Override
    public FeatureFlagResponse createFlag(CreateFeatureFlagRequest request){
        if(featureFlagRepository.existsByFlagKey(request.getFlagKey())){
            throw new FlagAlreadyExistsException(request.getFlagKey());
        }

        FeatureFlag featureFlag = FeatureFlag.builder()
                    .flagKey(request.getFlagKey())
                    .name(request.getName())
                    .description(request.getDescription())
                    .enabled(request.isEnabled())
                    .active(true)
                    .build();
        
        FeatureFlag savedFlag = featureFlagRepository.save(featureFlag);

        return mapToResponse(savedFlag);
    }

    @Override
    public List<FeatureFlagResponse> getAllFlags(){
        return featureFlagRepository.findByActiveTrue()
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly=true)
    public FeatureFlagStatusResponse getFlagStatus(String flagKey){
        FeatureFlag featureFlag = featureFlagRepository.findByFlagKeyAndActiveTrue(flagKey)
                                .orElseThrow(() -> new FlagNotFoundCustomException("flag key " + flagKey + " not found"));

        return FeatureFlagStatusResponse.builder()
                .flagkey(flagKey)
                .enabled(featureFlag.isEnabled())
                .build();
    }

    @Override
    public FeatureFlagStatusResponse updateFlagStatus(String flagKey, UpdateFeatureFlagStatusRequest request){
        FeatureFlag featureFlag = featureFlagRepository
                                .findByFlagKeyAndActiveTrue(flagKey)
                                .orElseThrow(() -> new FlagNotFoundCustomException("flag key " + flagKey + " not found"));
        System.out.println(featureFlag);
        featureFlag.setEnabled(request.getEnabled());
        featureFlagRepository.save(featureFlag);

        return FeatureFlagStatusResponse.builder()
                .flagkey(flagKey)
                .enabled(featureFlag.isEnabled())
                .build();

    }

    @Override
    @Transactional(readOnly = true)
    public FeatureFlagEvaluationResponse evaluateFlag(String flagKey, String userId){
        FeatureFlag featureFlag = featureFlagRepository.findByFlagKeyAndActiveTrue(flagKey)
                                .orElseThrow(() -> new FlagNotFoundCustomException("flag key " + flagKey + " not found"));
        
        if(!featureFlag.isEnabled()){
            return FeatureFlagEvaluationResponse.builder()
                .flagKey(featureFlag.getFlagKey())
                .enabled(featureFlag.isEnabled())
                .reason("DISABLED")
                .build();
        }

        int bucket = Math.abs(userId.hashCode()) % 100;

        boolean enabledForUser = bucket < featureFlag.getRolloutPercentage();

        return FeatureFlagEvaluationResponse.builder()
                .flagKey(featureFlag.getFlagKey())
                .enabled(enabledForUser)
                .reason(enabledForUser ? "ROLLOUT_MATCHED" : "ROLLOUT_NOT_MATCHED")
                .build();
        
    }

    @Override 
    public FeatureFlagStatusResponse updateRolloutPercentage(String flagKey, UpdateRolloutRequest request){
        FeatureFlag featureFlag = featureFlagRepository.findByFlagKeyAndActiveTrue(flagKey)
                                .orElseThrow(() -> new FlagNotFoundCustomException("flag key " + flagKey + " not found"));

        featureFlag.setRolloutPercentage(request.getRolloutPercentage());
        featureFlagRepository.save(featureFlag);

        return FeatureFlagStatusResponse.builder()
                .flagkey(featureFlag.getFlagKey())
                .enabled(featureFlag.isEnabled())
                .rolloutPercentage(featureFlag.getRolloutPercentage())
                .build();

    }

    @Override
    public RuleResponse addRule(String flagKey, AddRuleRequest request){

        FeatureFlag featureFlag = featureFlagRepository.findByFlagKeyAndActiveTrue(flagKey)
                                    .orElseThrow(() -> new FlagNotFoundCustomException("flag key " + flagKey + " not found"));
        
        FeatureFlagRule featureFlagRule = FeatureFlagRule.builder()
                                            .ruleType(request.getRuleType())
                                            .ruleValue(request.getRuleValue())
                                            .featureFlag(featureFlag)
                                            .build();
        FeatureFlagRule savedFlagRule = featureFlagRuleRepository.save(featureFlagRule);

        return RuleResponse.builder().id(savedFlagRule.getId())
                    .ruleType(savedFlagRule.getRuleType())
                    .ruleValue(savedFlagRule.getRuleValue())
                    .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleResponse> getRules(String flagKey){
        FeatureFlag featureFlag = featureFlagRepository.findByFlagKeyAndActiveTrue(flagKey)
                    .orElseThrow(() -> new FlagNotFoundCustomException("flag key " + flagKey + " not found"));
        
        return featureFlagRuleRepository.findByFeatureFlag(featureFlag)
                .stream().map(rule -> RuleResponse.builder()
                            .id(rule.getId())
                            .ruleType(rule.getRuleType())
                            .ruleValue(rule.getRuleValue())
                            .build()).toList();
    }

    @Override
    public void deleteRule(Long ruleId){
        featureFlagRuleRepository.deleteById(ruleId);
    }

    public FeatureFlagResponse mapToResponse(FeatureFlag featureFlag){
        FeatureFlagResponse response = FeatureFlagResponse.builder()
                        .id(featureFlag.getId())
                        .flagkey(featureFlag.getFlagKey())
                        .name(featureFlag.getName())
                        .description(featureFlag.getDescription())
                        .enabled(featureFlag.isEnabled())
                        .active(featureFlag.isActive())
                        .createdAt(featureFlag.getCreatedAt())
                        .updatedAt(featureFlag.getUpdatedAt())
                        .build();
        return response;
    }

   
}
