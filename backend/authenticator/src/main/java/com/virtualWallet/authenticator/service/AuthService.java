package com.virtualWallet.authenticator.service;

import com.virtualWallet.authenticator.dto.ResponseDTO;
import com.virtualWallet.authenticator.dto.TokenDTO;
import com.virtualWallet.authenticator.dto.TokenRequestDTO;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtService jwtService;

    public AuthService(JwtService jwtService) {
        this.jwtService = jwtService;
    }


    public ResponseDTO<String> generateToken(TokenRequestDTO tokenRequestDTO) {
        try {
            String token = jwtService.generateToken(tokenRequestDTO);
            return new ResponseDTO<>(true, null, token);
        } catch (Exception e){
            return new ResponseDTO<>(false, e.getMessage(), null);
        }
    }

    public TokenDTO authenticateToken(String token) {
        return jwtService.authenticateTokenAndGetData(token);
    }

}

