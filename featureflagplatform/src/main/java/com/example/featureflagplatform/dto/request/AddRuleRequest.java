package com.example.featureflagplatform.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class AddRuleRequest {

    @NotBlank(message = "Rule type cannot be blank")
    private String ruleType;

    @NotBlank(message = "Rule value cannot be blank")
    private String ruleValue;

    private String operatorType;
}
