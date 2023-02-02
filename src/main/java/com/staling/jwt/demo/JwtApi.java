package com.staling.jwt.demo;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;

import org.json.JSONObject;
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
import static io.jsonwebtoken.SignatureAlgorithm.*;

@RestController
@RequestMapping("/jwt")
public class JwtApi {

    @Value("${application.name}")
    private String applicationName;
    
    private KeyFactory keyFactory;

    @PostConstruct
    public void init() throws NoSuchAlgorithmException {
        keyFactory = KeyFactory.getInstance("RSA");
    }

    private static final Set<SignatureAlgorithm> SHARED_ALG = new HashSet<>(Arrays.asList(HS256, HS384, HS512));
    private static final Set<SignatureAlgorithm> ASYMMETRIC_ALG = new HashSet<>(Arrays.asList(RS256, RS384, RS512, PS256, PS384, PS512, ES256, ES384, ES512));

    @GetMapping("/generateKeys/{algorithm}")
    public String generateKeys(@PathVariable(required = false) SignatureAlgorithm algorithm) {
        if (algorithm == null) algorithm = SignatureAlgorithm.HS256;
        if (SHARED_ALG.contains(algorithm)) {
            SecretKey key = Keys.secretKeyFor(algorithm);
            String secretKey = Encoders.BASE64.encode(key.getEncoded());
            return new JSONObject()
                    .put("privateKey", secretKey)
                    .put("publicKey", secretKey)
                    .toString();
        } else if (ASYMMETRIC_ALG.contains(algorithm)) {
            KeyPair keyPair = Keys.keyPairFor(algorithm);
            return new JSONObject()
                    .put("privateKey", Encoders.BASE64.encode(keyPair.getPrivate().getEncoded()))
                    .put("publicKey", Encoders.BASE64.encode(keyPair.getPublic().getEncoded()))
                    .toString();
        } else {
            throw new ProgramException("Invalid algorithm");
        }
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

    @GetMapping("/generatePrivateToken")
    public String generatePrivateToken(String privateKey, String issuer) throws InvalidKeySpecException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Decoders.BASE64.decode(privateKey));
        PrivateKey key = keyFactory.generatePrivate(keySpec);
        return Jwts.builder()
            .setId(UUID.randomUUID().toString())
            .setIssuer(issuer)
            .setIssuedAt(new Date())
            .setAudience(applicationName)
            .signWith(key)
            .compact();
    }
}
