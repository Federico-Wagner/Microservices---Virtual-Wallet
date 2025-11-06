package com.billeteraVirtual.users.service;

import com.billeteraVirtual.users.Mapper.UserMapper;
import com.billeteraVirtual.users.dto.*;
import com.billeteraVirtual.users.entity.User;
import com.billeteraVirtual.users.enumerators.RolesEnum;
import com.billeteraVirtual.users.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    private final ExternalResoursesConnectionService externalResoursesConnectionService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(ExternalResoursesConnectionService externalResoursesConnectionService,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       UserMapper userMapper) {
        this.externalResoursesConnectionService = externalResoursesConnectionService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public ResponseDTO<String> login(LoginRequestDTO loginRequestDTO) {
        MDC.put("traceId", UUID.randomUUID().toString());
        MDC.put("userCuit", maskDni(loginRequestDTO.getDni()));
        log.info("LOGIN - Start authentication process");
        try {
            // Find user
            Optional<User> optionalUser = userRepository.findByDni(loginRequestDTO.getDni());
            if (optionalUser.isEmpty()) {
                log.warn("LOGIN - User not found");
                return new ResponseDTO<>(false, "Invalid credentials", null);
            }
            // Password validation
            User user = optionalUser.get();
            MDC.put("userId", user.getId().toString());
            boolean authenticated = passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword());
            if (!authenticated) {
                log.warn("LOGIN - Invalid password");
                return new ResponseDTO<>(false, "Invalid credentials", null);
            }
            // Token generation
            ResponseDTO<?> tokenResponseDTO = externalResoursesConnectionService.generateToken(
                    user.getId().toString(),
                    user.getDni(),
                    user.getRole().id
            );
            if (!tokenResponseDTO.isSuccess()) {
                log.error("LOGIN - Token generation failed");
                return new ResponseDTO<>(false, "Error generating token", null);
            }
            log.info("LOGIN - Authentication success | role={}", user.getRole().name());
            return new ResponseDTO<>(true, null, (String) tokenResponseDTO.getData());

        } catch (Exception e) {
            log.error("LOGIN - Unexpected error", e);
            return new ResponseDTO<>(false, "Internal server error", null);
        } finally {
            log.debug("LOGIN - Process completed");
        }
    }

    public ResponseDTO<?> registerNewClient(RegisterDTO registerDto) {
        try {
            MDC.put("traceId", UUID.randomUUID().toString());
            User newUser = new User();
            newUser.setName(registerDto.getName());
            newUser.setSurname(registerDto.getSurname());
            newUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));
            newUser.setDni(registerDto.getDni());
            newUser.setRole(RolesEnum.CLIENT);
            User newUserSaved = this.userRepository.save(newUser);
            log.info("NEW USER registered: {}", newUserSaved);
            return new ResponseDTO<>(true, null, null);
        } catch (Exception e) {
            log.error("ERROR - {}", e.getMessage());
            return new ResponseDTO<>(false, e.getMessage(), null);
        } finally {
            MDC.clear();
        }
    }

    public ResponseDTO<UserDTO> getUserDataToken(String token) {
        MDC.put("traceId", UUID.randomUUID().toString());
        log.info("GET_USER_DATA_TOKEN - Start process | token={}", maskToken(token));
        try {
            // Step 1: Auth token
            TokenDTO tokenDTO = externalResoursesConnectionService.authenticateToken(token);
            log.debug("Token authentication result | authenticated={} | userId={}", tokenDTO.isAuthenticated(), tokenDTO.getUserId());
            // Step 2: Auth validation
            if (!tokenDTO.isAuthenticated()) {
                log.warn("GET_USER_DATA_TOKEN - Token invalid or expired");
                return new ResponseDTO<>(false, "Token expired or invalid", null);
            }
            // Step 3: Get user data on DB
            Optional<User> userOptional = userRepository.findById(Long.valueOf(tokenDTO.getUserId()));
            if (userOptional.isEmpty()) {
                log.warn("GET_USER_DATA_TOKEN - User not found | userId={}", tokenDTO.getUserId());
                return new ResponseDTO<>(false, "User not found", null);
            }
            // Step 4: DTO conversion & password removal
            User user = userOptional.get();
            MDC.put("userId", user.getId().toString());
            UserDTO userDTO = userMapper.toDto(user);
            userDTO.setPassword(null);
            log.info("GET_USER_DATA_TOKEN - Success");
            return new ResponseDTO<>(true, null, userDTO);
        } catch (Exception e) {
            // Manejo centralizado de errores
            log.error("GET_USER_DATA_TOKEN - Unexpected error | message={}", e.getMessage(), e);
            return new ResponseDTO<>(false, "Internal server error", null);
        } finally {
            MDC.clear();
            log.debug("GET_USER_DATA_TOKEN - End process");
        }
    }

    private String maskDni(String dni) {
        if (dni == null || dni.length() <= 3) return "***";
        return dni.substring(0, 2) + "*****" + dni.substring(dni.length() - 1);
    }

    private String maskToken(String token) {
        if (token == null || token.length() < 10) return "****";
        return token.substring(0, 5) + "*****" + token.substring(token.length() - 3);
    }

}
