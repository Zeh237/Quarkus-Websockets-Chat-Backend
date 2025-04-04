package com.example.users.controller;

import com.example.auth.dto.AuthDto;
import com.example.users.dto.CreateUserDto;
import com.example.users.dto.LoginDto;
import com.example.users.dto.UpdateDto;
import com.example.users.dto.UserDetail;
import com.example.users.services.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/users")
public class UserController {

    @Inject
    UserService userService;

    @Inject
    JsonWebToken jwt;

    @POST
    public Response createUser(CreateUserDto dto){
        UserDetail userDetail = userService.createUser(dto);
        return Response.status(Response.Status.CREATED).entity(userDetail).build();
    }

    @POST
    @Path("/login")
    public Response login(LoginDto loginDto) {
        AuthDto authDto = userService.authenticate(loginDto);
        return Response.ok(authDto).build();
    }

    @GET
    @Path("/{id}")
    public UserDetail getUser(@PathParam("id") Long id) {
        return userService.getUserById(id);
    }

    @PATCH
    @Path("/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    public Response update(@PathParam("id") Long id, UpdateDto dto) {
        if (!jwt.getSubject().equals(id.toString())) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        UserDetail userDetail = userService.update(id, dto);
        return Response.ok(userDetail).build();
    }

}
