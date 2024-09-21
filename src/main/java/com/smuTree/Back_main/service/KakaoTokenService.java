package com.smuTree.Back_main.service;

import com.smuTree.Back_main.entity.KakaoToken;
import com.smuTree.Back_main.repository.KakaoTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KakaoTokenService {

    @Autowired
    private KakaoTokenRepository kakaoTokenRepository;

    public void saveKakaoToken(KakaoToken kakaoToken) {
        kakaoTokenRepository.save(kakaoToken);
    }
}
