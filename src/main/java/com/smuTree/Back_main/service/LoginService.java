package com.smuTree.Back_main.service;

import com.smuTree.Back_main.entity.Login;
import com.smuTree.Back_main.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service //카카오와 관련없는 파일
public class LoginService {

    @Autowired
    private LoginRepository loginRepository;

    public List<Login> getAllLogins() {
        return loginRepository.findAll();
    }

    public Optional<Login> getLoginById(Integer loginId) {
        return loginRepository.findById(loginId);
    }

    public Login saveLogin(Login login) {
        return loginRepository.save(login);
    }

    public void deleteLogin(Integer loginId) {
        loginRepository.deleteById(loginId);
    }
}
