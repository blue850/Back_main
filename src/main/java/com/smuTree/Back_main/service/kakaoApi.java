package com.smuTree.Back_main.service;
import org.springframework.beans.factory.annotation.Value;

public class kakaoApi {
    @Value("${kakao.api_key}")
    private String kakaoApiKey;

    @Value("${kakao.redirect_uri}")
    private String kakaoRedirectUri;
}
