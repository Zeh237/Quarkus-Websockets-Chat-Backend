package com.example.users.mapper;

import com.example.users.dto.ContactDto;
import com.example.users.model.Contacts;
import com.example.users.model.User;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ContactsMapper {

     public ContactDto toDto(Contacts contact) {
         if (contact == null) return null;
         ContactDto dto = new ContactDto();
         dto.setId(contact.getId());
         dto.setName(contact.getName());
         dto.setPhone(contact.getPhone());
         dto.setUser_id(contact.getUser().getId());
         return dto;
     }

     public Contacts toEntity(ContactDto dto) {
         if (dto == null) return null;
         Contacts contact = new Contacts();
         contact.setName(dto.getName());
         contact.setPhone(dto.getPhone());
         contact.setUser(User.findById(dto.getUser_id()));
         return contact;
     }
}
