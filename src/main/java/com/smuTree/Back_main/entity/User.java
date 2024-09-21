package com.smuTree.Back_main.entity;

import jakarta.persistence.*;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

@Entity
public class User {
    //key user_id, username, email, highscore, highscore_time

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer user_id;

    @Column
    private String username;

    @Column
    private String email;

    @Column
    private Integer highscore;

    @Column
    private Timestamp highscore_time;

}
