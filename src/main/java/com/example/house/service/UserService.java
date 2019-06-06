package com.example.house.service;

import com.example.house.domain.Role;
import com.example.house.domain.User;
import com.example.house.dto.UserDTO;
import com.example.house.repository.RoleRepository;
import com.example.house.repository.UserRepository;
import com.example.house.utils.UserUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public User findUserByName(String userName) {
        User user = userRepository.findByName(userName);
        if (user == null) {
            return null;
        }
        //查询设置权限
        setAuthority(user);

        return user;
    }

    private void setAuthority(User user) {
        List<Role> roles = roleRepository.findRolesByUserId(user.getId());
        if (roles != null && roles.isEmpty()) {
            throw new DisabledException("权限非法");
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));
        user.setAuthorityList(authorities);
    }

    @Override
    public UserDTO findById(String userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(user1 -> modelMapper.map(user1, UserDTO.class)).orElse(null);
    }

    private UserDTO modifyUserProfile(UserDTO userDTO) throws AccessDeniedException {
        User user = modelMapper.map(userDTO, User.class);
        String userId = UserUtils.getUserId();
        if (StringUtils.isEmpty(userId)) {
            throw new AccessDeniedException("无权访问");
        }
        user.setId(userId);
        User modifiedUser = userRepository.save(user);
        return modelMapper.map(modifiedUser, UserDTO.class);
    }

    public long total() {
        return userRepository.count();
    }
}
