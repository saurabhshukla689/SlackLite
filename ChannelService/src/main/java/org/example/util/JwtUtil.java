package org.example.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.dto.CurrentUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.UUID;

@Component
public class JwtUtil {
    private final String SECRET_KEY = "mySecretKey123456789012345678901234567890";
    private final int JWT_EXPIRATION = 86400000; // 24 hours

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }


    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Extract user info directly
    public CurrentUser getCurrentUserFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return new CurrentUser(
                UUID.fromString(claims.get("userId", String.class)),
                claims.get("username", String.class),
                claims.get("email", String.class)
        );
    }
}
