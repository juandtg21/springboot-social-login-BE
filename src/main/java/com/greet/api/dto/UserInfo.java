package com.greet.api.dto;

import lombok.Value;

import java.util.List;

@Value
public class UserInfo {
    String id, displayName, picture, email;
    List<String> roles;
}