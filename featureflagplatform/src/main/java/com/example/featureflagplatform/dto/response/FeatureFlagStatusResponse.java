package com.example.featureflagplatform.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeatureFlagStatusResponse {
    private String flagkey;

    private boolean enabled;
}
