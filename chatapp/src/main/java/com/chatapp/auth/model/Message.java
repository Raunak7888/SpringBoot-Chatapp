package com.chatapp.auth.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "sender_id", nullable = false)
    private Long senderId; // Stores the sender's ID

    @Column(name = "receiver_id")
    private Long receiverId; // Stores the receiver's ID for one-to-one messages

    @Column(name = "group_chat_id")
    private Long groupChatId; // Stores the group chat's ID for group messages

    public Message() {
        this.timestamp = LocalDateTime.now(); // Default to current time if no timestamp is provided
    }

    // Constructor for group messages
    public Message(String content, Long senderId, Long groupChatId, boolean isGroupMessage) {
        this.content = content;
        this.senderId = senderId;
        this.timestamp = LocalDateTime.now();

        if (isGroupMessage) {
            this.groupChatId = groupChatId;
        } else {
            this.receiverId = groupChatId; // In this case, `groupChatId` actually represents the receiver's ID
        }
    }
}
