package com.example.featureflagplatform.dto.response;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeatureFlagResponse {
    
    private Long id;

    private String flagkey;

    private String name;

    private String description;

    private boolean enabled;
    
    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Min(value = 0, message = "Rollout percentage cannot be less than 0")
    @Max(value = 100, message = "Rollout percentage cannot be greater than 100")
    private Integer rolloutPercentage;

}
