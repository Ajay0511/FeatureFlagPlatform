package com.example.featureflagplatform.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRolloutRequest {
    @Min(value = 0, message = "Rollout percentage cannot be less than 0")
    @Max(value = 100, message = "Rollout percentage cannot be greater than 100")
    private Integer rolloutPercentage;
}
