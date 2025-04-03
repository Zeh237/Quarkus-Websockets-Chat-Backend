package com.example.users.services;
import com.example.users.dao.UserDao;
import com.example.users.dto.CreateUserDto;
import com.example.users.mapper.UserMapper;
import com.example.users.dto.UserDetail;
import com.example.users.model.User;
import com.example.users.model.UserRole;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;


@ApplicationScoped
public class UserService {

    @Inject
    private UserDao userDao;

    @Inject
    private UserMapper userMapper;

    @Transactional
    public UserDetail createUser(CreateUserDto dto) {
        try {
            if (dto.getRole() == UserRole.ADMIN) {
                throw new ForbiddenException("Only admins can create admin accounts.");
            }
            User user = userMapper.toEntity(dto);
            user.setPassword(BcryptUtil.bcryptHash(dto.getPassword()));
            userDao.persist(user);
            return userMapper.toDTO(user);
        } catch (PersistenceException e) {
            if (e.getMessage().contains("constraint") &&
                    (e.getMessage().contains("username") || e.getMessage().contains("email"))) {
                throw new WebApplicationException("Username or email already exists", 400);
            }
            throw e;
        }
    }

    public UserDetail getUserById(Long id) {
        User user = userDao.findById(id);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return userMapper.toDTO(user);
    }
}
