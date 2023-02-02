package com.staling.jwt.demo;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import io.jsonwebtoken.io.Decoders;

@Component
public class JWTFilter implements Filter {

    @Value("${application.name}")
    private String applicationName;

    @Autowired
    @Qualifier("secretKeyResolver")
    private SigningKeyResolverAdapter secretKeyResolver;

    @Autowired
    @Qualifier("publicKeyResolver")
    private SigningKeyResolverAdapter publicKeyResolver;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        String path = request.getRequestURI().substring(request.getContextPath().length());
        if (path.startsWith("/api")) {
            String authorization = request.getHeader("Authorization");
            if (authorization == null || !authorization.startsWith("JWT ")) {
                throw new ProgramException("Missing token");
            }
            Jws<Claims> jwsCLaims = validateJWT(authorization.substring(4));
            String algorithm = jwsCLaims.getHeader().getAlgorithm();
            System.out.println(algorithm);
            String issuer = jwsCLaims.getBody().getIssuer();
            String audience = jwsCLaims.getBody().getAudience();
            if (!isValidUser(issuer)) {
                throw new ProgramException("Unknown user");
            }
            if (applicationName != null && !applicationName.equals(audience)) {
                throw new ProgramException("Token intended for different application");
            }
        }
        filterChain.doFilter(request, resp);
    }

    private boolean isValidUser(String issuer) {
        User user = userRepository.findByUserId(issuer);
        return user != null;
    }

    private Jws<Claims> validateJWT(String token) {
        String algorithm = new JSONObject(new String(Decoders.BASE64.decode(token.split("\\.")[0]))).getString("alg");
        return Jwts.parserBuilder()
                .setSigningKeyResolver(algorithm.startsWith("HS")? secretKeyResolver: publicKeyResolver)
                .build()
                .parseClaimsJws(token);
    }

}
