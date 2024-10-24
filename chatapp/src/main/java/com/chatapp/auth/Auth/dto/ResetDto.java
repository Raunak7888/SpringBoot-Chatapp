package com.chatapp.auth.Auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetDto {
    private String email;
    private String newPassword;
    private String passwordResetCode;
}
