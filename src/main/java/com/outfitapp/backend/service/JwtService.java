package com.outfitapp.backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
        "outfit-app-secret-key-muy-larga-para-seguridad-256-bits!!".getBytes()
    );
    private static final long EXPIRATION = 1000 * 60 * 60 * 24;

    public String generarToken(UserDetails usuario) {
        return Jwts.builder()
                .subject(usuario.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String extraerEmail(String token) {
        return getClaims(token).getSubject();
    }

    public boolean esTokenValido(String token, UserDetails usuario) {
        String email = extraerEmail(token);
        return email.equals(usuario.getUsername()) && !estaExpirado(token);
    }

    private boolean estaExpirado(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}