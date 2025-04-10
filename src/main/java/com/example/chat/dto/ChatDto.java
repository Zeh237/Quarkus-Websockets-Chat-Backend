package com.example.chat.dto;

import com.example.users.dto.UserDetail;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatDto {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private UserDetail sender;
    private UserDetail receiver;
    private MessageDto lastMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
