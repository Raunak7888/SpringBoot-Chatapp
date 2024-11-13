package com.chatapp.auth.chatapp.service;

import com.chatapp.auth.model.User;
import com.chatapp.auth.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatappUserService {

    private final UserRepository userRepository;

    public ChatappUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void setUserOnline(String username) {
        User user = findUserByUsername(username);
        user.setOnline(true);
        userRepository.save(user);
    }

    @Transactional
    public void setUserOffline(String username) {
        User user = findUserByUsername(username);
        user.setOnline(false);
        userRepository.save(user);
    }

    public boolean isUserOnline(String username) {
        return findUserByUsername(username).isOnline();
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }
}
