package com.example.featureflagplatform.service;

import org.springframework.stereotype.Service;

import com.example.featureflagplatform.dto.request.CreateFeatureFlagRequest;
import com.example.featureflagplatform.dto.response.FeatureFlagResponse;
import com.example.featureflagplatform.entity.FeatureFlag;
import com.example.featureflagplatform.exception.FlagAlreadyExistsException;
import com.example.featureflagplatform.repository.FeatureFlagRepository;

import jakarta.transaction.Transactional;
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
