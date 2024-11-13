package com.chatapp.auth.chatapp.controller;

import com.chatapp.auth.Auth.service.JwtService;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JwtChannelInterceptor.class);

    private final JwtService jwtService;

    @Autowired
    public JwtChannelInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
                String jwtToken = authHeader.substring(7);
                try {
                    String username = jwtService.extractUsername(jwtToken);

                    if (jwtService.isValid(jwtToken, username)) {
                        accessor.setUser(new UsernamePasswordAuthenticationToken(username, null, List.of()));
                        logger.info("User '{}' authenticated successfully via WebSocket", username);
                    } else {
                        logger.warn("Invalid JWT Token for user '{}'", username);
                        throw new IllegalArgumentException("Invalid JWT Token");
                    }
                } catch (Exception e) {
                    logger.error("WebSocket connection failed: {}", e.getMessage());
                    throw new IllegalArgumentException("Invalid JWT Token", e);
                }
            } else {
                logger.warn("Authorization header missing or incorrectly formatted in WebSocket connection");
            }
        }
        return message;
    }
}
