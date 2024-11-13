package com.chatapp.auth.chatapp.service;

import com.chatapp.auth.chatapp.DTO.MessageDTO;
import com.chatapp.auth.model.GroupChat;
import com.chatapp.auth.model.Message;
import com.chatapp.auth.model.User;
import com.chatapp.auth.repository.GroupChatRepository;
import com.chatapp.auth.repository.MessageRepository;
import com.chatapp.auth.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final GroupChatRepository groupChatRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository, GroupChatRepository groupChatRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.groupChatRepository = groupChatRepository;
    }

    @Transactional
    public Message saveMessage(MessageDTO messageDTO) {
        User sender = userRepository.findById(messageDTO.getSenderId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + messageDTO.getSenderId()));

        Message message = new Message();
        message.setContent(messageDTO.getContent());
        message.setTimestamp(LocalDateTime.now());
        message.setSender(sender);

        return messageRepository.save(message);
    }

    @Transactional
    public Message saveGroupMessage(MessageDTO messageDTO, Long groupId) {
        User sender = userRepository.findById(messageDTO.getSenderId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + messageDTO.getSenderId()));

        GroupChat groupChat = groupChatRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group chat not found with ID: " + groupId));

        Message message = new Message();
        message.setContent(messageDTO.getContent());
        message.setTimestamp(LocalDateTime.now());
        message.setSender(sender);
        message.setGroupChat(groupChat);

        return messageRepository.save(message);
    }
}
