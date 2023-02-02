package com.staling.jwt.demo;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import io.jsonwebtoken.io.Decoders;

@Component("publicKeyResolver")
public class JWTSigningPublicKeyResolver extends SigningKeyResolverAdapter {

    @Autowired
    private Environment env;

    private static KeyFactory keyFactory;

   static {
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Key resolveSigningKey(JwsHeader jwsHeader, Claims claims) {
        String publicString = env.getProperty("application.publicKeys." + claims.getIssuer());
        if (publicString == null) {
            throw new ProgramException("Invalid user");
        }
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Decoders.BASE64.decode(publicString));
        try {
            return keyFactory.generatePublic(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

}
