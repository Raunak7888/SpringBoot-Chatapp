package com.chatapp.auth.chatapp.listener;

import com.chatapp.auth.chatapp.service.ChatappUserService;
import lombok.NonNull;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Objects;

@Component
public class WebSocketEventListener extends TextWebSocketHandler {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatappUserService chatappUserService;

    private static final String STATUS_TOPIC = "/topic/status";

    public WebSocketEventListener(SimpMessagingTemplate messagingTemplate, ChatappUserService chatappUserService) {
        this.messagingTemplate = messagingTemplate;
        this.chatappUserService = chatappUserService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String username = getUsername(session);
        if (username != null) {
            chatappUserService.setUserOnline(username);
            messagingTemplate.convertAndSend(STATUS_TOPIC, username + " is online");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus status) {
        String username = getUsername(session);
        if (username != null) {
            chatappUserService.setUserOffline(username);
            messagingTemplate.convertAndSend(STATUS_TOPIC, username + " is offline");
        }
    }

    private String getUsername(WebSocketSession session) {
        try {
            return Objects.requireNonNull(session.getPrincipal()).getName();
        } catch (NullPointerException e) {
            System.err.println("Failed to retrieve username from session: " + e.getMessage());
            return null;
        }
    }
}
