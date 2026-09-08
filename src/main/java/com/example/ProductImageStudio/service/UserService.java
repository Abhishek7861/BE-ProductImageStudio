package com.example.ProductImageStudio.service;

import com.example.ProductImageStudio.entity.User;
import com.example.ProductImageStudio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Look up a user by email or create a fresh one, seeded from the Google
     * OAuth attributes. Also refreshes name/picture on every login so the
     * profile stays in sync with Google.
     */
    @Transactional
    public User findOrCreate(OAuth2User oauthUser) {
        String email = oauthUser.getAttribute("email");
        if (email == null) throw new IllegalArgumentException("OAuth user has no email");

        String name = oauthUser.getAttribute("name");
        String picture = oauthUser.getAttribute("picture");

        return userRepository.findByEmail(email)
                .map(u -> {
                    u.setName(name);
                    u.setPictureUrl(picture);
                    return u;
                })
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(email)
                                .name(name)
                                .pictureUrl(picture)
                                .build()
                ));
    }

    @Transactional
    public User debit(User user, BigDecimal amount) {
        if (user.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }
        user.setBalance(user.getBalance().subtract(amount));
        return userRepository.save(user);
    }

    @Transactional
    public User credit(User user, BigDecimal amount) {
        user.setBalance(user.getBalance().add(amount));
        return userRepository.save(user);
    }
}
