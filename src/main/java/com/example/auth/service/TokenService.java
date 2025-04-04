package com.example.auth.service;

import com.example.users.model.UserRole;
import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.jwt.Claims;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class TokenService {

    public static String generateToken(String username, Long userId, UserRole role) {
        Set<String> groups = new HashSet<>();
        groups.add(role.toString());

        return Jwt.issuer("my-app")
                .upn(username)
                .subject(userId.toString())
                .groups(groups)
                .claim(Claims.birthdate.name(), Instant.now().toString())
                .expiresIn(3600)
                .sign();
    }
}