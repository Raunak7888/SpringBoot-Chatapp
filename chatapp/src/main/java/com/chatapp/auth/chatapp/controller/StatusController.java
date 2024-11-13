package com.chatapp.auth.chatapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class StatusController {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public StatusController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyStatusChange(String username, boolean isOnline) {
        String statusMessage = username + (isOnline ? " is online" : " is offline");
        messagingTemplate.convertAndSend("/topic/status", statusMessage);
    }

    // Optionally, you can add other event handling methods here
}
