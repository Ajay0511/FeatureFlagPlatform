package com.example.featureflagplatform.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureFlagEvaluationRequestv2 {

    private String userId;

    private String email;

    private String country;

    private String companyId;

    private String plan;
}