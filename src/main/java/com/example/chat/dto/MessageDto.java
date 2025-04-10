package com.example.chat.dto;

import com.example.users.model.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDto {
    private Long id;
    private Long chatId;
    private String content;
    private boolean isRead;
    private Long senderId;
    private Long receiverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
