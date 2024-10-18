package com.example.JWTDemo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // Generate a secure key for HMAC-SHA256 (for signing the JWT)
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    //to generate a JWT
    public String generateToken(String userId) {
        //empty map to hold the claims -> claims are information about the entity(user) and additional data
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId) // username is getting stored here
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 5)) // 5 minutes expiration
                .signWith(key) // Use the generated secure key
                .compact();
    }

    public String extractUserName(String token) {
        //to extract the username
        return extractClaim(token, Claims::getSubject);
    }

    //extract any claim from the JWT based on the provided claim resolver function
    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);//get all claims from the token
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // Use the generated secure key
                .build().parseClaimsJws(token).getBody();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        // Check if the token has expired and if the username matches
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
