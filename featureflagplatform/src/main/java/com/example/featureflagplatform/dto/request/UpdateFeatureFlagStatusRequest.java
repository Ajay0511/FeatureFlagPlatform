package com.example.featureflagplatform.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateFeatureFlagStatusRequest {

    @NotNull(message = "enable is required")
    private Boolean enabled;

}
