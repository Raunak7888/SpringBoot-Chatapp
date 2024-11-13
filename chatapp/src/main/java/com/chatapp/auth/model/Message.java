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

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "group_chat_id")
    private GroupChat groupChat;

    public Message() {
        this.timestamp = LocalDateTime.now(); // Default to current time if no timestamp is provided
    }

    public Message(String content, User sender, GroupChat groupChat) {
        this.content = content;
        this.sender = sender;
        this.groupChat = groupChat;
        this.timestamp = LocalDateTime.now();
    }

    public Message(String content, User sender) {
        this(content, sender, null);
    }
}
