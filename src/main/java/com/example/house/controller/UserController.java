package com.example.house.controller;

import com.example.house.base.ApiResponse;
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
    public ApiResponse userInfo(@PathVariable("id") String userId) {
        log.info("获取用户详细信息id为{}", userId);
        UserDTO userDTO = userService.findById(userId);
        return ApiResponse.success(userDTO);
    }

    @GetMapping("/api/users")
    public ApiResponse allUsers() {
        Iterable<User> all = userRepository.findAll();
        return ApiResponse.success(all);
    }
}
