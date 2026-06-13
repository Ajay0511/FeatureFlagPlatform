package com.example.featureflagplatform.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.featureflagplatform.dto.request.CreateFeatureFlagRequest;
import com.example.featureflagplatform.dto.response.FeatureFlagResponse;
import com.example.featureflagplatform.service.FeatureFlagService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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

}
