package com.example.featureflagplatform.exception;

public class FlagNotFoundCustomException extends RuntimeException {
    public FlagNotFoundCustomException(String message){
        super(message);
    }
}
