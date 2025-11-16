package com.example.PatasyColas.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    // --- CAMBIO 1: Este es ahora el Access Token (Corta Duración) ---
    // (Tu método 'getToken' ahora se llama 'generateToken')
    public String generateToken(UserDetails userDetails) {
        // Duración corta: 15 minutos (puedes ajustarlo)
        long expirationTime = 1000L * 60 * 15; 
        return createToken(new HashMap<>(), userDetails.getUsername(), expirationTime);
    }

    // --- ¡NUEVO! : Este es el Refresh Token (Larga Duración) ---
    public String generateRefreshToken(UserDetails userDetails) {
        // Duración larga: 30 días
        long expirationTime = 1000L * 60 * 60 * 24 * 30; 
        return createToken(new HashMap<>(), userDetails.getUsername(), expirationTime);
    }

    // --- CAMBIO 2: Método privado que crea CUALQUIER token ---
    // (Tu antiguo 'getToken' privado, ahora refactorizado)
    private String createToken(Map<String, Object> extraClaims, String subject, long expirationTime) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // Usa el tiempo de expiración que le pasemos
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) 
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // --- (El resto de tus métodos para leer y validar quedan exactamente igual) ---

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Claims getAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }
}