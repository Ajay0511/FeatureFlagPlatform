package com.example.featureflagplatform.dto.request;

import lombok.Data;

@Data
public class FeatureFlagEvaluationRequest {
    private String userId;

    private String email;
}
