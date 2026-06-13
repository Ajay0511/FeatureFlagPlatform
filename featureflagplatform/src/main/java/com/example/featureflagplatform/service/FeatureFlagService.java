package com.example.featureflagplatform.service;

import com.example.featureflagplatform.dto.request.CreateFeatureFlagRequest;
import com.example.featureflagplatform.dto.response.FeatureFlagResponse;

public interface FeatureFlagService {
    FeatureFlagResponse createFlag(CreateFeatureFlagRequest request);
}
