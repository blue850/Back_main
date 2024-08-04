package com.smuTree.Back_main.entity;

import jakarta.persistence.*;

@Entity
public class Login {
    //key login_id, user_id, Provider, social_id

    @Id
    private Integer login_id; //사용자의 소셜로그인 방식에 따른 고유 아이디

    @Column
    private Integer user_id; //게임 내 사용자 고유 아이디

    @Column
    private String social_id; //소셜 서비스 아이디

    @Enumerated(EnumType.STRING)
    @Column
    private Provider provider; //소셜 서비스 (네이버, 카카오...)

    public Integer getLogin_id() {
        return login_id;
    }

    public void setLogin_id(Integer login_id) {
        this.login_id = login_id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getSocial_id() {
        return social_id;
    }

    public void setSocial_id(String social_id) {
        this.social_id = social_id;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }
}
