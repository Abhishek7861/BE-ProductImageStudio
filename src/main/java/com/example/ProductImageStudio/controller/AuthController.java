package com.example.ProductImageStudio.controller;

import com.example.ProductImageStudio.entity.User;
import com.example.ProductImageStudio.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/api/v1/auth/me")
    public Map<String, Object> me(@AuthenticationPrincipal OAuth2User oauthUser) {
        User user = userService.findOrCreate(oauthUser);

        return Map.of(
                "id",      user.getId(),
                "name",    user.getName(),
                "email",   user.getEmail(),
                "picture", user.getPictureUrl(),
                "balance", user.getBalance()
        );
    }
}
