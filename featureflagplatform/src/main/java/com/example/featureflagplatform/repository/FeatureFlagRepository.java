package com.example.featureflagplatform.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.featureflagplatform.entity.FeatureFlag;

public interface FeatureFlagRepository extends JpaRepository<FeatureFlag, Long> {
    
    boolean existsByFlagKey(String flagKey);

    Optional<FeatureFlag> findByIdAndActiveTrue(Long id);

    Optional<FeatureFlag> findByFlagKeyAndActiveTrue(String flagKey);

    List<FeatureFlag> findByActiveTrue();

}
