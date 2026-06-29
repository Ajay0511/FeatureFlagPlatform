package com.example.featureflagplatform.service;

import java.util.List;

import com.example.featureflagplatform.dto.request.AddRuleRequest;
import com.example.featureflagplatform.dto.request.CreateFeatureFlagRequest;
import com.example.featureflagplatform.dto.request.FeatureFlagEvaluationRequestv2;
import com.example.featureflagplatform.dto.request.UpdateFeatureFlagStatusRequest;
import com.example.featureflagplatform.dto.request.UpdateRolloutRequest;
import com.example.featureflagplatform.dto.response.FeatureFlagEvaluationResponse;
import com.example.featureflagplatform.dto.response.FeatureFlagResponse;
import com.example.featureflagplatform.dto.response.FeatureFlagStatusResponse;
import com.example.featureflagplatform.dto.response.RuleResponse;

public interface FeatureFlagService {
    FeatureFlagResponse createFlag(CreateFeatureFlagRequest request);

    List<FeatureFlagResponse> getAllFlags();

    FeatureFlagStatusResponse getFlagStatus(String flagKey);

    FeatureFlagStatusResponse updateFlagStatus(String flagKey, UpdateFeatureFlagStatusRequest request);

    FeatureFlagEvaluationResponse evaluateFlag(String flagKey, String userId);

    FeatureFlagStatusResponse updateRolloutPercentage(String flagKey, UpdateRolloutRequest request);

    RuleResponse addRule(String flagKey, AddRuleRequest request);

    List<RuleResponse> getRules(String flagKey);

    void deleteRule(Long ruleId);

    FeatureFlagEvaluationResponse evaluateFlagv2(String flagKey, FeatureFlagEvaluationRequestv2 request);
}
