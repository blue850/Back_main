package com.smuTree.Back_main.controller;

import com.smuTree.Back_main.service.LoginService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Value("${kakao.client_id}")
    private String kakaoClientId;

    @Value("${kakao.redirect.uri}")
    private String redirectUri;

    @Value("${frontend.ip}")
    private String frontendIp;

    @Value("${frontend.port}")
    private String frontendPort;

    // ... (other methods remain the same)

    @GetMapping("/login/oauth/kakao")
    public void kakaoCallback(@RequestParam("code") String code, HttpServletResponse response) {
        try {
            String accessToken = getKakaoAccessToken(code);
            log.info("Received Kakao authorization code: {}", code);
            log.info("Retrieved access token: {}", accessToken);

            // Request user information using the access token
            KakaoUserInfo userInfo = getKakaoUserInfo(accessToken);
            log.info("Retrieved Kakao user info: {}", userInfo);

            // TODO: Save user information to the database
            // loginService.saveKakaoUser(userInfo);

            // Redirect to the frontend application
            response.sendRedirect(String.format("http://%s:%s/InGame", frontendIp, frontendPort));
        } catch (Exception e) {
            log.error("Error during Kakao login process", e);
            try {
                response.sendRedirect(String.format("http://%s:%s/error", frontendIp, frontendPort));
            } catch (Exception redirectError) {
                log.error("Error redirecting to error page", redirectError);
            }
        }
    }

    public String getKakaoAccessToken(String code) {
        String accessTokenUrl = "https://kauth.kakao.com/oauth/token";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<KakaoTokenResponse> response = restTemplate.exchange(
                accessTokenUrl,
                HttpMethod.POST,
                kakaoTokenRequest,
                KakaoTokenResponse.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            KakaoTokenResponse tokenResponse = response.getBody();
            log.info("Successfully retrieved Kakao tokens: {}", tokenResponse);
            return tokenResponse.getAccess_token();
        } else {
            log.error("Failed to retrieve Kakao tokens. Status code: {}", response.getStatusCode());
            throw new RuntimeException("Failed to retrieve Kakao tokens");
        }
    }

    private KakaoUserInfo getKakaoUserInfo(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(
                userInfoUrl,
                HttpMethod.GET,
                entity,
                KakaoUserInfo.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("Successfully retrieved Kakao user info");
            return response.getBody();
        } else {
            log.error("Failed to retrieve Kakao user info. Status code: {}", response.getStatusCode());
            throw new RuntimeException("Failed to retrieve Kakao user info");
        }
    }

    @Getter
    @Setter
    private static class KakaoTokenResponse {
        private String token_type;
        private String access_token;
        private int expires_in;
        private String refresh_token;
        private int refresh_token_expires_in;
    }

    @Getter
    @Setter
    private static class KakaoUserInfo {
        private Long id;
        private Properties properties;
        private KakaoAccount kakao_account;

        @Getter
        @Setter
        public static class Properties {
            private String nickname;
        }

        @Getter
        @Setter
        public static class KakaoAccount {
            private String email;
        }
    }
}