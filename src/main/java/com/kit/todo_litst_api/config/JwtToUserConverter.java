package com.kit.todo_litst_api.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import java.util.Collections;

/**
 * Converts the JwtObject into User object for use in SecurityContext
 */
@Component
public class JwtToUserConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());

        return new UsernamePasswordAuthenticationToken(userId, jwt, Collections.emptyList());
    }
}
