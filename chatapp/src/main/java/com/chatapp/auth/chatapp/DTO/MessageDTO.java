package com.chatapp.auth.chatapp.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MessageDTO {
    private Long id;

    @NotNull(message = "Content cannot be null")
    private String content;

    private LocalDateTime timestamp;

    @NotNull(message = "Sender ID cannot be null")
    private Long senderId;

    private Long groupChatId;
}
