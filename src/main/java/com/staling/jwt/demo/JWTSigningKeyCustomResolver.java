package com.staling.jwt.demo;

import java.security.Key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTSigningKeyCustomResolver extends SigningKeyResolverAdapter {

    @Autowired
    private Environment env;

    @Override
    @SuppressWarnings("rawtypes")
    public Key resolveSigningKey(JwsHeader jwsHeader, Claims claims) {
        String secretString = env.getProperty("application.secretKeys." + claims.getIssuer());
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretString));
    }

}
