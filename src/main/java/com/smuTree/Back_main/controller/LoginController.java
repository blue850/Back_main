package com.smuTree.Back_main.controller;

import com.smuTree.Back_main.entity.Login;
import com.smuTree.Back_main.entity.Provider;
import com.smuTree.Back_main.service.LoginService;
import com.smuTree.Back_main.service.KakaoApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private KakaoApi kakaoApi;

    @Value("${kakao.client_id}")
    private String kakaoClientId;

    @Value("${kakao.redirect.uri}")
    private String redirectUri;

    // ... 다른 메서드들은 그대로 유지 ...

    @GetMapping("/kakaoLogin")
    public String kakaoLogin(Model model) {
        model.addAttribute("kakaoClientId", kakaoClientId);
        model.addAttribute("redirectUri", redirectUri);
        return "kakaoLogin";
    }

    @GetMapping("/login/oauth/kakao")
    public String kakaoCallback(@RequestParam("code") String code) {
        // 1. 인가 코드 받기 (이미 @RequestParam으로 받음)

        // 2. 토큰 받기
        String accessToken = kakaoApi.getAccessToken(code);

        // 3. 사용자 정보 받기
        Map<String, Object> userInfo = kakaoApi.getUserInfo(accessToken);

        String email = (String) userInfo.get("email");
        String nickname = (String) userInfo.get("nickname");

        System.out.println("email = " + email);
        System.out.println("nickname = " + nickname);
        System.out.println("accessToken = " + accessToken);

        // TODO: 여기에 사용자 정보를 이용한 로그인 또는 회원가입 로직 추가

        return "redirect:/result";
    }
}