package com.example.habittracker.service;

import com.example.habittracker.dto.response.UserResponse;
import com.example.habittracker.entity.User;
import com.example.habittracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
        .getAuthentication()
        .getName();

        return userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public UserResponse getCurrentUserResponse() {
        User user = getCurrentUser();

        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail()
        );
    }
}