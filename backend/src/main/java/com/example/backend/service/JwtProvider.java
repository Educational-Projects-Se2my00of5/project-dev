package com.example.backend.service;

import com.example.backend.exception.AuthenticationException;
import com.example.backend.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.util.Date;


@Service
public class JwtProvider {

    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;
    @Getter
    private final long jwtAccessExpiration;
    @Getter
    private final long jwtRefreshExpiration;


    public JwtProvider(
            @Value("${jwt.secret.access}") String jwtAccessSecret,
            @Value("${jwt.secret.refresh}") String jwtRefreshSecret,
            @Value("${jwt.expiration.access}") long jwtAccessExpiration,
            @Value("${jwt.expiration.refresh}") long jwtRefreshExpiration
    ) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
        this.jwtAccessExpiration = jwtAccessExpiration;
        this.jwtRefreshExpiration = jwtRefreshExpiration;
    }

    public boolean validateAccessToken(String accessToken) {
        return validateToken(accessToken, jwtAccessSecret);
    }

    public boolean validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken, jwtRefreshSecret);
    }

    private boolean validateToken(String token, Key secret) {
        try {
            Jwts.parser()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            //log.error("Token expired", expEx);
            throw new AuthenticationException("Token expired");
        } catch (UnsupportedJwtException unsEx) {
            //log.error("Unsupported jwt", unsEx);
            throw new AuthenticationException("Unsupported jwt");
        } catch (MalformedJwtException mjEx) {
            //log.error("Malformed jwt", mjEx);
            throw new AuthenticationException("Malformed jwt");
        } catch (SignatureException sEx) {
            //log.error("Invalid signature", sEx);
            throw new AuthenticationException("Invalid signature");
        } catch (Exception e) {
            //log.error("invalid token", e);
            throw new AuthenticationException("invalid token");
        }
    }

    public Claims getAccessClaims(String token) {
        return getClaims(token, jwtAccessSecret);
    }

    public Claims getRefreshClaims(String token) {
        return getClaims(token, jwtRefreshSecret);
    }

    private Claims getClaims(String token, Key secret) {
        return Jwts.parser()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getEmailFromAuthHeader(String authHeader) {
        Claims claims = getAccessClaims(getTokenFromAuthHeader(authHeader));
        return claims.getSubject();
    }

    public Long getUserIdFromAuthHeader(String authHeader) {
        Claims claims = getAccessClaims(getTokenFromAuthHeader(authHeader));
        return (Long) claims.get("userId");
    }

    public String getTokenFromAuthHeader(String authHeader) {
        if (authHeader == null || authHeader.isEmpty())
            throw new AuthenticationException("Not contain Authorization header");
        String token = null;
        if (authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        return token;
    }

    public String generateAccessToken(User user) {
        final Instant accessExpirationInstant = Instant.now().plusSeconds(jwtAccessExpiration);
        final Date accessExpiration = Date.from(accessExpirationInstant);
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(accessExpiration)
                .signWith(jwtAccessSecret)
                .claim("userId", user.getId().toString())
                .claim("username", user.getUsername())
                .claim("roles", user.getRoles())
                .compact();
    }

    public String generateRefreshToken(User user) {
        final Instant refreshExpirationInstant = Instant.now().plusSeconds(jwtRefreshExpiration);
        final Date refreshExpiration = Date.from(refreshExpirationInstant);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(refreshExpiration)
                .signWith(jwtRefreshSecret)
                .compact();
    }
}