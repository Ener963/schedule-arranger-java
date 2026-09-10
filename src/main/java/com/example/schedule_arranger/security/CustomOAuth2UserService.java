package com.example.schedule_arranger.security;

import com.example.schedule_arranger.entity.User;
import com.example.schedule_arranger.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Integer githubUserId = ((Number) oAuth2User.getAttribute("id")).intValue();
        String githubLogin = oAuth2User.getAttribute("login");

        User user = userRepository.findById(githubUserId).orElseGet(User::new);
        user.setUserId(githubUserId);
        user.setUsername(githubLogin);
        userRepository.save(user);

        return oAuth2User;
    }
}
