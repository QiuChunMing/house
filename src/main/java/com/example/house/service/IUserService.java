package com.example.house.service;

import com.example.house.domain.User;
import com.example.house.dto.UserDTO;

public interface IUserService {
    User findUserByName(String userName);

    UserDTO findById(String userId);
}
