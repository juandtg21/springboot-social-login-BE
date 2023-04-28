package com.greet.api.dto;

import lombok.Value;

@Value
public class ApiResponse {
    Boolean success;
    String message;
}
