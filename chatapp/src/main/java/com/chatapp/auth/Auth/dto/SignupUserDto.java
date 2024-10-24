package com.chatapp.auth.Auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupUserDto {
    private String email;
    private String password;
    private String username;
}
