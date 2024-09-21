package com.smuTree.Back_main.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class KakaoToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accessToken;
    private String tokenType;
    private String refreshToken;
    private int expiresIn;
    private int refreshTokenExpiresIn;
}