package com.example.featureflagplatform.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.featureflagplatform.dto.request.AddRuleRequest;
import com.example.featureflagplatform.dto.request.CreateFeatureFlagRequest;
import com.example.featureflagplatform.dto.request.FeatureFlagEvaluationRequestv2;
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
import com.example.featureflagplatform.helper.FeatureFlagHelper;
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
    public FeatureFlagEvaluationResponse evaluateFlag(String flagKey, String userId) {

        FeatureFlag featureFlag = featureFlagRepository
                .findByFlagKeyAndActiveTrue(flagKey)
                .orElseThrow(() ->
                        new FlagNotFoundCustomException("flag key " + flagKey + " not found"));

        /*
        * Step 1 : Flag disabled globally
        */
        if (!featureFlag.isEnabled()) {
            return FeatureFlagEvaluationResponse.builder()
                    .flagKey(flagKey)
                    .enabled(false)
                    .reason("DISABLED")
                    .build();
        }

        /*
        * Step 2 : Explicit user targeting overrides everything
        */
        List<FeatureFlagRule> rules =
                featureFlagRuleRepository.findByFeatureFlag(featureFlag);

        boolean userMatched = rules.stream()
                .filter(rule -> "USER_ID".equalsIgnoreCase(rule.getRuleType()))
                .anyMatch(rule -> userId.equals(rule.getRuleValue()));

        if (userMatched) {
            return FeatureFlagEvaluationResponse.builder()
                    .flagKey(flagKey)
                    .enabled(true)
                    .reason("USER_RULE_MATCHED")
                    .build();
        }

        /*
        * Step 3 : Percentage rollout
        */
        int bucket = Math.abs(userId.hashCode()) % 100;

        boolean rolloutMatched =
                bucket < featureFlag.getRolloutPercentage();

        if (rolloutMatched) {
            return FeatureFlagEvaluationResponse.builder()
                    .flagKey(flagKey)
                    .enabled(true)
                    .reason("ROLLOUT_MATCHED")
                    .build();
        }

        /*
        * Step 4 : Deny
        */
        return FeatureFlagEvaluationResponse.builder()
                .flagKey(flagKey)
                .enabled(false)
                .reason("ROLLOUT_NOT_MATCHED")
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
                                            .operatorType(request.getOperatorType())
                                            .ruleValue(request.getRuleValue())
                                            .featureFlag(featureFlag)
                                            .build();
        FeatureFlagRule savedFlagRule = featureFlagRuleRepository.save(featureFlagRule);

        return RuleResponse.builder().id(savedFlagRule.getId())
                    .ruleType(savedFlagRule.getRuleType())
                    .ruleValue(savedFlagRule.getRuleValue())
                    .operatorType(savedFlagRule.getOperatorType())
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

    @Override
    @Transactional(readOnly = true)
    public FeatureFlagEvaluationResponse evaluateFlagv2(String flagKey, FeatureFlagEvaluationRequestv2 request){
        //return evaluateFlag(flagKey, request.getUserId());
        FeatureFlag featureFlag = featureFlagRepository.findByFlagKeyAndActiveTrue(flagKey).orElseThrow(
            () -> new FlagNotFoundCustomException("Flag key " + flagKey + " not found")
        );

        //Flag Disabled globally
        if(!featureFlag.isEnabled()){
            return FeatureFlagEvaluationResponse.builder()
                .flagKey(featureFlag.getFlagKey())
                .enabled(false)
                .reason("DISABLED")
                .build();
        }

        //check on userId
        List<FeatureFlagRule> rules = featureFlagRuleRepository.findByFeatureFlag(featureFlag);

        boolean userIdMatched = rules.stream()
                    .filter(rule -> "USER_ID".equalsIgnoreCase(rule.getRuleType()))
                    .anyMatch(rule -> FeatureFlagHelper.evaluateRule(
                        request.getUserId(),
                        rule.getOperatorType(),
                        rule.getRuleValue()
                ));
        
        if(userIdMatched){
            return FeatureFlagEvaluationResponse.builder()
                    .flagKey(flagKey)
                    .enabled(true)
                    .reason("USER_ID Rule Matched")
                    .build();
        }

        /*
        * EMAIL
        */
        boolean emailMatched = rules.stream()
                .filter(rule ->
                        "EMAIL".equalsIgnoreCase(rule.getRuleType()))
                .anyMatch(rule ->
                        FeatureFlagHelper.evaluateRule(request.getEmail(), 
                        rule.getOperatorType(), 
                        rule.getRuleValue()));

        if (emailMatched) {
            return FeatureFlagEvaluationResponse.builder()
                    .flagKey(flagKey)
                    .enabled(true)
                    .reason("EMAIL_RULE_MATCHED")
                    .build();
        }

        /*
        * COUNTRY
        */
        boolean countryMatched = rules.stream()
                .filter(rule ->
                        "COUNTRY".equalsIgnoreCase(rule.getRuleType()))
                .anyMatch(rule ->
                        FeatureFlagHelper.evaluateRule(request.getCountry(), 
                        rule.getOperatorType(),
                        rule.getRuleValue()));

        if (countryMatched) {
            return FeatureFlagEvaluationResponse.builder()
                    .flagKey(flagKey)
                    .enabled(true)
                    .reason("COUNTRY_RULE_MATCHED")
                    .build();
        }

        /*
        * COMPANY_ID
        */
        boolean companyMatched = rules.stream()
                .filter(rule ->
                        "COMPANY_ID".equalsIgnoreCase(rule.getRuleType()))
                .anyMatch(rule ->
                        FeatureFlagHelper.evaluateRule(request.getCompanyId(), 
                        rule.getOperatorType(), 
                        rule.getRuleValue()));

        if (companyMatched) {
            return FeatureFlagEvaluationResponse.builder()
                    .flagKey(flagKey)
                    .enabled(true)
                    .reason("COMPANY_RULE_MATCHED")
                    .build();
        }

        /*
        * PLAN
        */
        boolean planMatched = rules.stream()
                .filter(rule ->
                        "PLAN".equalsIgnoreCase(rule.getRuleType()))
                .anyMatch(rule ->
                        FeatureFlagHelper.evaluateRule(request.getPlan(), 
                        rule.getOperatorType(), 
                        rule.getRuleValue()));

        if (planMatched) {
            return FeatureFlagEvaluationResponse.builder()
                    .flagKey(flagKey)
                    .enabled(true)
                    .reason("PLAN_RULE_MATCHED")
                    .build();
        }

        /*
        * Rollout fallback
        */
        if (request.getUserId() == null) {
            return FeatureFlagEvaluationResponse.builder()
                    .flagKey(flagKey)
                    .enabled(false)
                    .reason("USER_ID_REQUIRED_FOR_ROLLOUT")
                    .build();
        }

        int bucket =
                Math.abs(request.getUserId().hashCode()) % 100;

        boolean rolloutMatched =
                bucket < featureFlag.getRolloutPercentage();

        return FeatureFlagEvaluationResponse.builder()
                .flagKey(flagKey)
                .enabled(rolloutMatched)
                .reason(
                        rolloutMatched
                                ? "ROLLOUT_MATCHED"
                                : "ROLLOUT_NOT_MATCHED")
                .rolloutPercentage(
                        featureFlag.getRolloutPercentage())
                .build();

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
