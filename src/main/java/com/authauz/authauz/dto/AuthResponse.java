package com.authauz.authauz.dto;

import com.authauz.authauz.common.Role;
import com.authauz.authauz.common.UserType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthResponse {
    private String username;
    private UserType userType;
    private Role role;
    private String token;
}
