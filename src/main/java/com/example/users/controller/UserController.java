package com.example.users.controller;

import com.example.users.dto.CreateUserDto;
import com.example.users.dto.UserDetail;
import com.example.users.services.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
public class UserController {

    @Inject
    UserService userService;

    @POST
    public Response createUser(CreateUserDto dto){
        UserDetail userDetail = userService.createUser(dto);
        return Response.status(Response.Status.CREATED).entity(userDetail).build();
    }

    @GET
    @Path("/{id}")
    public UserDetail getUser(@PathParam("id") Long id) {
        return userService.getUserById(id);
    }

}
