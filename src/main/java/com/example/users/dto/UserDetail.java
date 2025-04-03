package com.example.users.dto;
import com.example.users.model.UserRole;
import lombok.Data;

@Data
public class UserDetail {
    private String username;
    private String email;
    private UserRole role;
}
