package com.example.flyhas.util;

import com.example.flyhas.model.BaseUser;
import com.example.flyhas.model.Customer;
import com.example.flyhas.model.Manager;
import com.example.flyhas.model.Admin;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenUtil {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long expirationMillis = 86400000;

    public String generateToken(BaseUser user) {
        String role;
        if (user instanceof Customer) {
            role = "CUSTOMER";
        } else if (user instanceof Manager) {
            role = "MANAGER";
        } else if (user instanceof Admin) {
            role = "ADMIN";
        } else {
            role = "UNKNOWN";
        }

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", role)
                .claim("firstName", user.getFirstName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String extractRole(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role");
    }

    public String extractFirstName(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("firstName");
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}
