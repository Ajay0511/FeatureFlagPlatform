package com.example.featureflagplatform.helper;

import java.util.List;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FeatureFlagHelper {

    public boolean evaluateRule(
            String actualValue,
            String operator,
            String expectedValue) {

        if (actualValue == null) {
            return false;
        }

        switch (operator.toUpperCase()) {

            case "EQUALS":
                return actualValue.equalsIgnoreCase(expectedValue);

            case "NOT_EQUALS":
                return !actualValue.equalsIgnoreCase(expectedValue);

            case "CONTAINS":
                return actualValue.contains(expectedValue);

            case "STARTS_WITH":
                return actualValue.startsWith(expectedValue);

            case "ENDS_WITH":
                return actualValue.endsWith(expectedValue);

            case "IN":
                return List.of(expectedValue.split(","))
                        .stream()
                        .map(String::trim)
                        .anyMatch(value ->
                                value.equalsIgnoreCase(actualValue));

            default:
                return false;
        }
    }
}