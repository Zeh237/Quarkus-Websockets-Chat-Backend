package com.example.chat.controller;

import com.example.chat.dto.ChatDto;
import com.example.chat.dto.MessageDto;
import com.example.chat.services.ChatService;
import com.example.chat.services.WebsocketService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Path("/chat")
public class ChatController {
    @Inject
    private ChatService chatService;

    @Inject
    private WebsocketService websocketService;

    @Inject
    JsonWebToken jwt;

    @POST
    @Transactional
    public Response sendMessage(MessageDto messageDto) {
        try {
            if (messageDto.getContent() == null || messageDto.getContent().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Message content cannot be empty")
                        .build();
            }
            MessageDto dto = chatService.sendMessage(messageDto);
            if (websocketService.isUserOnline(messageDto.getReceiverId())) {
                websocketService.sendMessageToUser(messageDto.getReceiverId(), messageDto);
            }

            return Response.ok(dto).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getChats(@PathParam("id") Long id, @QueryParam("page") int page, @QueryParam("size") int size){
        if (!jwt.getSubject().equals(id.toString())) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        page = page-1;
        List<ChatDto> chats = chatService.getChats(id, page, size);
        return Response.ok(chats).build();
    }
}
