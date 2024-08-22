package com.smuTree.Back_main.controller;

import com.smuTree.Back_main.entity.Login;
import com.smuTree.Back_main.entity.Provider;
import com.smuTree.Back_main.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

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
    public String kakaoLogin(Model model){
        return "kakaoLogin";
    }
}
