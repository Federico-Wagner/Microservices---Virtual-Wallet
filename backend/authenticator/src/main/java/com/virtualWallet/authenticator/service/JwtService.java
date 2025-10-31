package com.virtualWallet.authenticator.service;

import com.virtualWallet.authenticator.dto.TokenDTO;
import com.virtualWallet.authenticator.dto.TokenRequestDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
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
    private Long TOKEN_DURATION_MINUTES;

    private static final String USER_ID_TAG = "USER_ID";
    private static final String USER_CUIT_TAG = "USER_CUIT";
    private static final String ROLE_TAG = "ROLE";

    public String generateToken(TokenRequestDTO tokenRequestDTO) {
        // ROL en TOKEN
        Map<String, Object> claims = new HashMap<>();
        claims.put(USER_ID_TAG, tokenRequestDTO.getUserId());
        claims.put(USER_CUIT_TAG, tokenRequestDTO.getUserCuit());
        claims.put(ROLE_TAG, tokenRequestDTO.getRolesEnum());
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_DURATION_MINUTES * 60_000))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .compact();
    }

    public TokenDTO authenticateTokenAndGetData(String token) {
        TokenDTO tokenDTO = new TokenDTO(token);
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            tokenDTO.setAuthenticated(true);
            tokenDTO.setUserId(claims.get(USER_ID_TAG, String.class));
            tokenDTO.setUserCuit(claims.get(USER_CUIT_TAG, String.class));
            tokenDTO.setUserRole(claims.get(ROLE_TAG, String.class));
        } catch (JwtException e) {
            log.info("invalid or expired token: {}", e.getMessage());
            tokenDTO.setAuthenticated(false);
        }
        return tokenDTO;
    }

}
