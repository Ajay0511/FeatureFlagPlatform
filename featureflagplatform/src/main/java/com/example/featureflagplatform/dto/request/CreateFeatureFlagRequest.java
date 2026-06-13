package com.example.featureflagplatform.dto.request;

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

}
