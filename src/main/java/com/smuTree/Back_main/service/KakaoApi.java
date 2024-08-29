package com.smuTree.Back_main.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Objects;

@Service
public class KakaoApi {
    @Value("${kakao.client_id}")
    String kakaoClientId;

    @Value("${kakao.redirect.uri}")
    String kakaoRedirectUri;

    private final String tokenUrl = "https://kauth.kakao.com/oauth/token";
    private final String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
    private final String logoutUrl = "https://kapi.kakao.com/v1/user/logout";

    private final RestTemplate restTemplate = new RestTemplate();

    // 인가 코드를 받아서 accessToken을 반환
    public String getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<HashMap> response = restTemplate.postForEntity(tokenUrl, request, HashMap.class);

        return (String) Objects.requireNonNull(response.getBody()).get("access_token");
    }

    // accessToken을 받아서 UserInfo 반환
    public HashMap getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        ResponseEntity<HashMap> response = restTemplate.exchange(
                userInfoUrl,
                HttpMethod.GET,
                request,
                HashMap.class
        );

        return response.getBody();
    }

    // accessToken을 받아서 로그아웃 시키는 메서드
    public void kakaoLogout(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                logoutUrl,
                HttpMethod.POST,
                request,
                String.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to logout from Kakao");
        }
    }
}