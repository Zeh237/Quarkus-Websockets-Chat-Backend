package com.example.users.mapper;

import com.example.users.dto.CreateUserDto;
import com.example.users.dto.UpdateDto;
import com.example.users.dto.UserDetail;
import com.example.users.model.User;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserMapper {
    public UserDetail toDTO(User user) {
        if (user == null) return null;

        UserDetail userDetail = new UserDetail();
        userDetail.setEmail(user.getEmail());
        userDetail.setUsername(user.getUsername());
        userDetail.setRole(user.getRole());
        userDetail.setId(user.getId());
        return userDetail;
    }

    public User toEntity(CreateUserDto dto) {
        if (dto == null) return null;

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        return user;
    }

    public User updateToEntity(UpdateDto dto) {
        if (dto == null) return null;

        User user = new User();
        if (dto.getUsername() != null) {
            user.setUsername(dto.getUsername());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getPassword() != null) {
            user.setPassword(dto.getPassword());
        }
        return user;
    }
}
