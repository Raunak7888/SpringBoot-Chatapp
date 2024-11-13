package com.chatapp.auth.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "group_chats")
public class GroupChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "groupChats", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "groupChat", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Message> messages = new HashSet<>();

    public GroupChat() {
    }

    public GroupChat(String name) {
        this.name = name;
    }

    // Utility methods for managing bidirectional relationships

    public void addUser(User user) {
        users.add(user);
        user.getGroupChats().add(this);
    }

    public void removeUser(User user) {
        users.remove(user);
        user.getGroupChats().remove(this);
    }

    public void addMessage(Message message) {
        messages.add(message);
        message.setGroupChat(this);
    }

    public void removeMessage(Message message) {
        messages.remove(message);
        message.setGroupChat(null);
    }
}
