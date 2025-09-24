package com.virtualWallet.AuthenticatorMs.service;

import com.virtualWallet.AuthenticatorMs.enumerators.RolesEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class JwtService {


    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.duration-minutes}")
    private Long SECRET_KEY_DURATION_MINUTES;

    private final String roleStrTag = "ROLE";

    public String generateToken(String userIdentification, RolesEnum role) {
        // ROL en TOKEN
        Map<String, Object> claims = new HashMap<>();
        claims.put(roleStrTag, role.id);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userIdentification)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * SECRET_KEY_DURATION_MINUTES)) // 1 hora
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String validateTokenAndGetUserIdentificator(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            log.info(e.getMessage());
            return null; // token inválido o expirado
        }
    }

    public RolesEnum getTokenRole(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes())
                .parseClaimsJws(token)
                .getBody();
        return RolesEnum.getEnum(claims.get(roleStrTag, String.class));
    }

}
