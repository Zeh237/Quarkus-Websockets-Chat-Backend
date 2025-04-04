package com.example.auth.dto;

import com.example.users.dto.UserDetail;
import lombok.Data;

@Data
public class AuthDto {
    private String token;
    private UserDetail user;
}
