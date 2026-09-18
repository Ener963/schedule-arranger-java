package com.example.schedule_arranger.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * JS 版 src/routes/login.js 相当。
 * 更新後の login.js はログイン中ユーザー表示を行わなくなったため、
 * ここも GitHub ログインボタンを表示するだけのシンプルな作りにしている。
 */
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}