package com.example.users.services;
import com.example.auth.service.TokenService;
import com.example.auth.dto.AuthDto;
import com.example.users.dao.UserDao;
import com.example.users.dto.CreateUserDto;
import com.example.users.dto.LoginDto;
import com.example.users.dto.UpdateDto;
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

import java.time.LocalDateTime;


@ApplicationScoped
public class UserService {

    @Inject
    private UserDao userDao;

    @Inject
    private UserMapper userMapper;

    @Inject
    private WebSocketService webSocketService;

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

    public AuthDto authenticate(LoginDto loginDto) {
        User user = userDao.findByUsername(loginDto.getUsername());

        if (user == null || !BcryptUtil.matches(loginDto.getPassword(), user.getPassword())) {
            throw new WebApplicationException("Invalid username or password", 401);
        }

        try {
            String token = TokenService.generateToken(
                    user.getUsername(),
                    user.getId(),
                    user.getRole()
            );

            AuthDto auth = new AuthDto();
            auth.setToken(token);
            auth.setUser(userMapper.toDTO(user));
            return auth;
        } catch (Exception e) {
            throw new WebApplicationException("Token generation failed", 500);
        }
    }

    public UserDetail update(Long id, UpdateDto dto){
        User user = userDao.findById(id);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        if (dto.getUsername() != null) {
            user.setUsername(dto.getUsername());
        }
        if (dto.getPassword() != null) {
            user.setPassword(BcryptUtil.bcryptHash(dto.getPassword()));
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        userDao.persist(user);

        return userMapper.toDTO(user);
    }

    public String getLastSeen(Long id) {
        User user = userDao.findById(id);
        if (user == null) {
            return "User not found";
        }

        if (webSocketService.isUserOnline(id)) {
            return "Online";
        }

        LocalDateTime lastSeen = user.getLastSeen();
        if (lastSeen == null) {
            return "Long time ago";
        }

        return lastSeen.toString();
    }
}
