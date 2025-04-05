package com.example.users.dao;

import com.example.users.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserDao implements PanacheRepository<User> {
    public User findByEmail(String email) {
        return find("email", email).firstResult();
    }
    public User findById(Long id){return find("id", id).firstResult();}
    public User findByUsername(String username){return find("username", username).firstResult();}
}
