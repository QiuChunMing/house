package com.example.house.repository;

import com.example.house.domain.Role;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface RoleRepository extends PagingAndSortingRepository<Role,String> {
    List<Role> findRolesByUserId(String userId);
}
