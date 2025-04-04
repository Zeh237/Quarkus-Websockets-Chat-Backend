package com.example.users.dto;
import lombok.Data;

@Data
public class UpdateDto {
    private String username;
    private String password;
    private String email;
}
