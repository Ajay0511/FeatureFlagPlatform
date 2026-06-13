package com.example.featureflagplatform.dto.response;

import java.time.LocalDateTime;

import jakarta.persistence.Id;
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

}
