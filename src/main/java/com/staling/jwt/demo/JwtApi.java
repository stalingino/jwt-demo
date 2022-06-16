package com.staling.jwt.demo;

import java.security.KeyPair;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

@RestController
@RequestMapping("/jwt")
public class JwtApi {

    @Value("${application.name}")
    private String applicationName;

    @GetMapping("/generateSecretKey/{algorithm}")
    public String generateSecretKey(@PathVariable(required = false) SignatureAlgorithm algorithm) {
        if (algorithm == null) algorithm = SignatureAlgorithm.HS256;
        SecretKey key = Keys.secretKeyFor(algorithm);
        return Encoders.BASE64.encode(key.getEncoded());
    }

    @GetMapping("/generateAsymmetricKeys/{algorithm}")
    public String generateAsymmetricKeys(@PathVariable(required = false) SignatureAlgorithm algorithm) {
        if (algorithm == null) algorithm = SignatureAlgorithm.RS256;
        KeyPair keyPair = Keys.keyPairFor(algorithm);
        return "Private Key:\n" + Encoders.BASE64.encode(keyPair.getPrivate().getEncoded()) + "\n\nPublic Key:\n" + 
                Encoders.BASE64.encode(keyPair.getPublic().getEncoded());
    }

    @GetMapping("/generateToken")
    public String generateToken(String secret, String issuer) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        return Jwts.builder()
            .setId(UUID.randomUUID().toString())
            .setIssuer(issuer)
            .setIssuedAt(new Date())
            .setAudience(applicationName)
            .signWith(key)
            .compact();
    }
}
