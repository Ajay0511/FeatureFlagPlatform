package com.example.featureflagplatform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleResponse {
    
    private Long id;

    private String ruleType;

    private String ruleValue;

    private String operatorType;

}
