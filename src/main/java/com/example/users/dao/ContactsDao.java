package com.example.users.dao;

import com.example.users.model.Contacts;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ContactsDao implements PanacheRepository<Contacts> {
    public Contacts findById(Long id) {
        return find("id", id).firstResult();
    }

    public List<Contacts> findByUserId(Long userId, int page, int size) {
        return find("user.id", userId).page(page, size).list();
    }

    public void deleteByUserId(Long userId) {
        delete("user.id", userId);
    }
}
