package com.example.featureflagplatform.service;

import java.util.List;

import com.example.featureflagplatform.dto.request.CreateFeatureFlagRequest;
import com.example.featureflagplatform.dto.request.UpdateFeatureFlagStatusRequest;
import com.example.featureflagplatform.dto.response.FeatureFlagResponse;
import com.example.featureflagplatform.dto.response.FeatureFlagStatusResponse;

public interface FeatureFlagService {
    FeatureFlagResponse createFlag(CreateFeatureFlagRequest request);

    List<FeatureFlagResponse> getAllFlags();

    FeatureFlagStatusResponse getFlagStatus(String flagKey);

    FeatureFlagStatusResponse updateFlagStatus(String flagKey, UpdateFeatureFlagStatusRequest request);
}
