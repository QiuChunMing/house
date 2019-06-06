package com.example.house.utils;

import com.example.house.domain.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtils {
    public static User load() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }
        return null;
    }

    public static String getUserId() {
        User user = load();
        if (user == null) {
            return null;
        } else {
            return user.getId();
        }
    }
}
