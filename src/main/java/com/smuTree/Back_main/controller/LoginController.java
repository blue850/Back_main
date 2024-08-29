package com.smuTree.Back_main.controller;

import com.smuTree.Back_main.entity.Login;
import com.smuTree.Back_main.entity.Provider;
import com.smuTree.Back_main.service.LoginService;
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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

//    @Autowired
//    private KakaoUserService kakaoUserService;

    @Value("${kakao.client_id}")
    private String kakaoClientId;

    @Value("${kakao.redirect.uri}")
    private String redirectUri;

    @GetMapping("/loginForm")
    public String showLoginForm(Model model) {
        List<Provider> providers = Arrays.asList(Provider.values());
        model.addAttribute("providers", providers);
        return "loginForm";
    }

    @PostMapping("/loginForm")
    public String submitLogin(@ModelAttribute Login login, Model model) {
        loginService.saveLogin(login);
        model.addAttribute("login", login);
        return "result";
    }

    @GetMapping("/kakaoLogin")
    public String kakaoLogin(Model model) {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize"
                + "?client_id=" + kakaoClientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code";
        model.addAttribute("kakaoAuthUrl", kakaoAuthUrl);
        return "kakaoLogin";  // kakaoLogin.html 템플릿을 렌더링
    }

    @GetMapping("/login/oauth/kakao")
    public String kakaoCallback(@RequestParam("code") String code) {
        try {
            // 액세스 토큰 요청
            String accessToken = getKakaoAccessToken(code);

            log.info("Received Kakao authorization code: {}", code);
            log.info("Retrieved access token: {}", accessToken);

            // 사용자 정보 요청
            KakaoUserInfo userInfo = getKakaoUserInfo(accessToken);

            log.info("Kakao user info: {}", userInfo);

            // DB에 저장 // kakaoUserService.java 안 만들어서 우선 주석처리
            // TODO
//            kakaoUserService.saveKakaoUserInfo(kakaoUserInfo);

            return "hello"; // 성공 페이지로 리다이렉트
        } catch (Exception e) {
            log.error("Error during Kakao login process", e);
            return "error"; // 에러 페이지로 리다이렉트
        }
    }

    private String getKakaoAccessToken(String code) {
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
            log.info("Successfully retrieved Kakao access token");
            return Objects.requireNonNull(response.getBody()).getAccess_token();
        } else {
            log.error("Failed to retrieve Kakao access token. Status code: {}", response.getStatusCode());
            throw new RuntimeException("Failed to retrieve Kakao access token");
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

        return response.getBody();
    }

    @Getter
    @Setter
    private static class KakaoUserInfo {
        private Long id;
        private String connected_at;
        private Properties properties;
        private KakaoAccount kakao_account;

        @Getter
        @Setter
        private static class Properties {
            private String nickname;
        }

        @Getter
        @Setter
        private static class KakaoAccount {
            private Boolean profile_nickname_needs_agreement;
            private Boolean profile_image_needs_agreement;
            private Profile profile;
            private Boolean has_email;
            private Boolean email_needs_agreement;
            private Boolean is_email_valid;
            private Boolean is_email_verified;
            private String email;

            @Getter
            @Setter
            private static class Profile {
                private String nickname;
                private String thumbnail_image_url;
                private String profile_image_url;
                private Boolean is_default_image;
            }
        }
    }

    @Getter
    @Setter
    private static class KakaoTokenResponse {
        private String access_token;
        private String token_type;
        private String refresh_token;
        private int expires_in;
        private String scope;
        private int refresh_token_expires_in;
    }
}