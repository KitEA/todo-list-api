package com.kit.todo_litst_api.config;

import com.kit.todo_litst_api.model.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collections;

public class UserAuthentication extends AbstractAuthenticationToken {
    private final User user;
    private final String userId;

    public UserAuthentication(User user, Long userId, Jwt credentials) {
        super(Collections.emptyList());
        this.user = user;
        this.userId = userId.toString();
        setAuthenticated(true);
        setDetails(credentials);
    }

    @Override
    public Object getPrincipal() {
        return user;
    }

    @Override
    public Object getCredentials() {
        return getDetails();
    }

    @Override
    public String getName() {
        return userId; // return the ID without touching the proxy
    }
}
