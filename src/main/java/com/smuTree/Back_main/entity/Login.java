package com.smuTree.Back_main.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Login {
    //key login_id, user_id, Provider, social_id

    @Id
    private String login_id;

    @Column
    private String user_id;

    @Column
    private String social_id;

}
