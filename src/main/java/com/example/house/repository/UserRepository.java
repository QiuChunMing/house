package com.example.house.repository;

import com.example.house.domain.User;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepository extends PagingAndSortingRepository<User,String> {
    User findByName(String name);
}
