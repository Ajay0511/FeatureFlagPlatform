package com.example.featureflagplatform.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateFeatureFlagRequest {
    
    @NotBlank(message = "Feature Key must not be blank")
    private String flagKey;

    @NotBlank(message = "Feature name must not be blank")
    private String name;

    private String description;

    private boolean enabled;

    @Min(value = 0, message = "Rollout percentage cannot be less than 0")
    @Max(value = 100, message = "Rollout percentage cannot be greater than 100")
    private Integer rolloutPercentage;

}
