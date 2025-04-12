package com.example.users.controller;

import com.example.users.dao.ContactsDao;
import com.example.users.dto.ContactDto;
import com.example.users.mapper.ContactsMapper;
import com.example.users.model.Contacts;
import com.example.users.services.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.stream.Collectors;

@Path("/contacts")
public class ContactController {

     @Inject
     ContactsDao contactsDao;

     @Inject
     ContactsMapper contactsMapper;

     @Inject
     private UserService userService;

    @Inject
    JsonWebToken jwt;

     @POST
     @RolesAllowed({"USER", "ADMIN"})
     public Response createContact(ContactDto contactDto) {
         if (!jwt.getSubject().equals(contactDto.getUser_id().toString())) {
             return Response.status(Response.Status.FORBIDDEN).build();
         }
        return Response.ok(userService.createContacts(contactDto)).build();
     }

    @GET
    @RolesAllowed({"USER", "ADMIN"})
    @Path("/{id}")
    public Response getAllContacts(@PathParam("id") Long id, @QueryParam("page") int page) {
        if (!jwt.getSubject().equals(id.toString())) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return Response.ok(userService.getContacts(id, page)).build();
    }

     @GET
     @RolesAllowed({"USER", "ADMIN"})
     @Path("/{id}/all")
     public Response getContactById(@PathParam("id") Long id) {
         Contacts contact = contactsDao.findById(id);
         if (contact == null) {
             return Response.status(Response.Status.NOT_FOUND).build();
         }
         return Response.ok(contactsMapper.toDto(contact)).build();
     }

     @PATCH
     @RolesAllowed({"USER", "ADMIN"})
     public Response updateContact(ContactDto contactDto) {
         if (!jwt.getSubject().equals(contactDto.getUser_id().toString())) {
             return Response.status(Response.Status.FORBIDDEN).build();
         }
         return Response.ok(userService.updateContact(contactDto)).build();
     }

    @DELETE
    @Path("delete/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    public Response deleteContact(@PathParam("id") Long id) {
        if (!jwt.getSubject().equals(id.toString())) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        userService.deleteContact(id);
        return Response.ok().build();
    }

    @GET
    @Path("/bulk")
    @RolesAllowed({"USER", "ADMIN"})
    public Response bulkImportContacts(List<ContactDto> contacts) {
        return Response.ok(userService.bulkImportContacts(contacts)).build();
    }
}
