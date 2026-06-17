package com.example.featureflagplatform.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.featureflagplatform.dto.request.CreateFeatureFlagRequest;
import com.example.featureflagplatform.dto.request.UpdateFeatureFlagStatusRequest;
import com.example.featureflagplatform.dto.request.UpdateRolloutRequest;
import com.example.featureflagplatform.dto.response.FeatureFlagEvaluationResponse;
import com.example.featureflagplatform.dto.response.FeatureFlagResponse;
import com.example.featureflagplatform.dto.response.FeatureFlagStatusResponse;
import com.example.featureflagplatform.service.FeatureFlagService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/v1/flags")
@RequiredArgsConstructor
public class FeatureFlagController {
    private final FeatureFlagService featureFlagService;

    @PostMapping("/createflag")
    @ResponseStatus(HttpStatus.CREATED)
    public FeatureFlagResponse createFlag(@Valid @RequestBody CreateFeatureFlagRequest request){
        return featureFlagService.createFlag(request);
    }

    @GetMapping("/all")
    public ResponseEntity<List<FeatureFlagResponse>> getAllFlags(){
        return ResponseEntity.ok(featureFlagService.getAllFlags());
    }

    @GetMapping("/{flagKey}/status")
    public ResponseEntity<FeatureFlagStatusResponse> getFlagStatus(@PathVariable String flagKey){
        System.out.println(flagKey);
        return ResponseEntity.ok(featureFlagService.getFlagStatus(flagKey));
    }

    @PatchMapping("/{flagKey}/update")
    public ResponseEntity<FeatureFlagStatusResponse> updateFlagStatus(
            @PathVariable String flagKey,
            @RequestBody UpdateFeatureFlagStatusRequest request
    ){
        return ResponseEntity.ok(featureFlagService.updateFlagStatus(flagKey, request));
    }

    @GetMapping("/{flagKey}/evaluate")
    public ResponseEntity<FeatureFlagEvaluationResponse> evaluateflag(
        @PathVariable String flagKey,
        @RequestParam String userId){
            return ResponseEntity.ok(featureFlagService.evaluateFlag(flagKey, userId));
    }

    @PatchMapping("/{flagKey}/rollout")
    public ResponseEntity<FeatureFlagStatusResponse> updateRollout(
            @PathVariable String flagKey,
            @Valid @RequestBody UpdateRolloutRequest request) {

        return ResponseEntity.ok(
                featureFlagService.updateRolloutPercentage(flagKey, request));
    }
}
