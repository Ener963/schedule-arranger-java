package com.example.schedule_arranger.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * layout.js のナビゲーションバー（ログイン中なら「${user.login} をログアウト」、
 * そうでなければ「ログイン」を表示）は全ページ共通のため、
 * 各コントローラーで毎回セットする代わりにここで一括して Model に追加する。
 */
@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("githubLogin")
    public String githubLogin(@AuthenticationPrincipal OAuth2User principal) {
        return principal != null ? principal.getAttribute("login") : null;
    }
}
