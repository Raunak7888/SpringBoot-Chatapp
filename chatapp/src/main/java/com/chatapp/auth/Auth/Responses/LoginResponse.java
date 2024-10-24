package com.chatapp.auth.Auth.Responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String token;
    private String expiresIn;

    public LoginResponse(String token, String expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }
}
