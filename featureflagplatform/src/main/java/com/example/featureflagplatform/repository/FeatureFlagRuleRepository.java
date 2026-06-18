package com.example.featureflagplatform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.featureflagplatform.entity.FeatureFlag;
import com.example.featureflagplatform.entity.FeatureFlagRule;

public interface FeatureFlagRuleRepository extends JpaRepository<FeatureFlagRule, Long> {
    List<FeatureFlagRule> findByFeatureFlag(FeatureFlag featureFlag);
}
