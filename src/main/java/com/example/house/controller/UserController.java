package com.example.house.controller;

import com.example.house.domain.User;
import com.example.house.dto.UserDTO;
import com.example.house.repository.UserRepository;
import com.example.house.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/api/user/who")
    public Authentication who(Authentication authentication) {
        return authentication;
    }

    @GetMapping("/api/userinfo/{id}")
    public UserDTO userInfo(@PathVariable("id") String userId) {
        log.info("获取用户详细信息id为{}", userId);
        return userService.findById(userId);
    }

    @GetMapping("/api/users")
    public Iterable<User> allUsers() {
        return userRepository.findAll();
    }
}
