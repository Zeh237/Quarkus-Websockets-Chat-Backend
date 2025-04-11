package com.example.users.dto;
import com.example.users.model.UserRole;
import lombok.Data;

@Data
public class UserDetail {
    private Long id;
    private String username;
    private String email;
    private Long phone;
    private UserRole role;
}
