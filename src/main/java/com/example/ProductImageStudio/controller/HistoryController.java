package com.example.ProductImageStudio.controller;

import com.example.ProductImageStudio.dto.HistoryItem;
import com.example.ProductImageStudio.dto.PageResponse;
import com.example.ProductImageStudio.entity.User;
import com.example.ProductImageStudio.repository.JobRepository;
import com.example.ProductImageStudio.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class HistoryController {

    private final UserService userService;
    private final JobRepository jobRepository;

    @GetMapping("/history")
    public PageResponse<HistoryItem> history(
            @AuthenticationPrincipal OAuth2User oauthUser,
            @PageableDefault(size = 10) Pageable pageable) {

        User user = userService.findOrCreate(oauthUser);
        return PageResponse.from(
                jobRepository.findByUserAndImageUrlIsNotNullOrderByCreatedAtDesc(user, pageable),
                HistoryItem::from
        );
    }
}
