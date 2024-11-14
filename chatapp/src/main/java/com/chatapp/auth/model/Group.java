package com.chatapp.auth.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Groups")
public class Group {

    private Long id;

    private String name;




}
