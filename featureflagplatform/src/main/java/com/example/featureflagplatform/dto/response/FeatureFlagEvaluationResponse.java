package com.example.featureflagplatform.dto.response;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeatureFlagEvaluationResponse {
    
    private String flagKey;

    private boolean enabled;

    private String reason;

    @Min(value = 0, message = "Rollout percentage cannot be less than 0")
    @Max(value = 100, message = "Rollout percentage cannot be greater than 100")
    private Integer rolloutPercentage;
}
