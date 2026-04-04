package com.chetraseng.sunrise_task_flow_api.services;

import com.chetraseng.sunrise_task_flow_api.config.JwtProperties;
import com.chetraseng.sunrise_task_flow_api.model.UserModel;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtService {

    public Object generateToken(UserModel user) {
        return null;
    }

    public String extractUsername(String token) {
        return token;
    }

    public boolean isValid(String token, UserDetails user) {
        return false;
    }

    @Service
    @RequiredArgsConstructor
    public static class jwtService {

        private final JwtProperties jwtProperties;

        private SecretKey getKey() {
            return Keys.hmacShaKeyFor(
                    Decoders.BASE64.decode(jwtProperties.getSecret()));
        }

        public String generateToken(UserDetails user) {
            return Jwts.builder()
                    .subject(user.getUsername())
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                    .signWith(getKey())
                    .compact();
        }

        public String extractUsername(String token) {
            return Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        }

        public boolean isValid(String token, UserDetails user) {
            return extractUsername(token).equals(user.getUsername());
        }
    }

}
