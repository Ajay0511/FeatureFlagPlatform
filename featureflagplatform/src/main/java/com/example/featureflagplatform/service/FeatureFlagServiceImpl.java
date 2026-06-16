package com.example.featureflagplatform.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.featureflagplatform.dto.request.CreateFeatureFlagRequest;
import com.example.featureflagplatform.dto.request.UpdateFeatureFlagStatusRequest;
import com.example.featureflagplatform.dto.response.FeatureFlagResponse;
import com.example.featureflagplatform.dto.response.FeatureFlagStatusResponse;
import com.example.featureflagplatform.entity.FeatureFlag;
import com.example.featureflagplatform.exception.FlagAlreadyExistsException;
import com.example.featureflagplatform.exception.FlagNotFoundCustomException;
import com.example.featureflagplatform.repository.FeatureFlagRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
@Transactional
public class FeatureFlagServiceImpl implements FeatureFlagService {
 
    private final FeatureFlagRepository featureFlagRepository;

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
