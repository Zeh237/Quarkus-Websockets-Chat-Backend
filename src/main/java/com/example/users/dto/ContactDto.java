package com.example.users.dto;

import lombok.Data;

@Data
public class ContactDto {
    private Long id;
    private String name;
    private Long phone;
    private Long user_id;
}
