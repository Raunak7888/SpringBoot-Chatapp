package com.chatapp.auth.chatapp.controller;

import com.chatapp.auth.chatapp.DTO.MessageDTO;
import com.chatapp.auth.chatapp.service.MessageService;
import com.chatapp.auth.model.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ChatController {

    private final MessageService messageService;

    public ChatController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Handles sending of a public message.
     * @param messageDTO DTO containing message content and sender information.
     * @return MessageDTO representing the saved message.
     */
    @MessageMapping("/send/message")
    @SendTo("/topic/messages")
    public MessageDTO sendMessage(MessageDTO messageDTO) {
        // Save the message to the database
        Message savedMessage = messageService.saveMessage(messageDTO);
        return convertToDTO(savedMessage);
    }

    /**
     * Handles sending of a group message.
     * @param messageDTO DTO containing message content and sender information.
     * @param groupId ID of the group to which the message is being sent.
     * @return MessageDTO representing the saved group message.
     */
    @MessageMapping("/send/group/{groupId}")
    @SendTo("/topic/group/{groupId}")
    public MessageDTO sendGroupMessage(MessageDTO messageDTO, @PathVariable Long groupId) {
        // Save the group message to the database
        Message savedMessage = messageService.saveGroupMessage(messageDTO, groupId);
        return convertToDTO(savedMessage);
    }

    /**
     * Converts a Message entity to a MessageDTO.
     * @param message The message entity to convert.
     * @return MessageDTO representing the message.
     */
    private MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setContent(message.getContent());
        dto.setTimestamp(message.getTimestamp());
        dto.setSenderId(message.getSender().getId());
        dto.setGroupChatId(message.getGroupChat() != null ? message.getGroupChat().getId() : null);
        return dto;
    }
}
