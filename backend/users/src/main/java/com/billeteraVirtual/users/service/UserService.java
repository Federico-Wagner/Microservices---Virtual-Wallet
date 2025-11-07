package com.billeteraVirtual.users.service;

import com.billeteraVirtual.users.Mapper.UserMapper;
import com.billeteraVirtual.users.dto.*;
import com.billeteraVirtual.users.entity.User;
import com.billeteraVirtual.users.enumerators.RolesEnum;
import com.billeteraVirtual.users.observability.Metrics;
import com.billeteraVirtual.users.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final ExternalResoursesConnectionService externalResoursesConnectionService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final Metrics metrics;

    public UserService(ExternalResoursesConnectionService externalResoursesConnectionService,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       UserMapper userMapper,
                       Metrics metrics) {
        this.externalResoursesConnectionService = externalResoursesConnectionService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.metrics = metrics;
    }

    public ResponseDTO<String> login(LoginRequestDTO loginRequestDTO) {
        try (var ignored = metrics.trace("LOGIN")) {
            log.info("Start authentication process");
            return metrics.getLoginTimer().record(() -> {
                try {
                    // Find user
                    Optional<User> optionalUser = userRepository.findByDni(loginRequestDTO.getDni());
                    if (optionalUser.isEmpty()) {
                        log.warn("User not found");
                        this.metrics.getLoginFailure().increment();
                        return new ResponseDTO<>(false, "Invalid credentials", null);
                    }
                    // Password validation
                    User user = optionalUser.get();
                    MDC.put("userId", user.getId().toString());
                    boolean authenticated = passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword());
                    if (!authenticated) {
                        log.warn("Invalid password");
                        this.metrics.getLoginFailure().increment();
                        return new ResponseDTO<>(false, "Invalid credentials", null);
                    }
                    // Token generation
                    ResponseDTO<?> tokenResponseDTO = externalResoursesConnectionService.generateToken(
                            user.getId().toString(), user.getDni(), user.getRole().id);
                    if (!tokenResponseDTO.isSuccess()) {
                        log.error("Token generation failed");
                        this.metrics.getLoginFailure().increment();
                        return new ResponseDTO<>(false, "Error generating token", null);
                    }
                    log.info("Authentication success | role={}", user.getRole().name());
                    this.metrics.getLoginSuccess().increment();
                    return new ResponseDTO<>(true, null, (String) tokenResponseDTO.getData());
                } catch (Exception e) {
                    log.error("Unexpected error", e);
                    this.metrics.getLoginFailure().increment();
                    return new ResponseDTO<>(false, "Internal server error", null);
                } finally {
                    log.debug("Process completed");
                }
            });
        }
    }

    public ResponseDTO<?> signup(RegisterDTO registerDto) {
        try (var ignored = metrics.trace("SIGN_UP")) {
            log.info("Start process");
            return metrics.getSignUpTimer().record(() -> {
                try {
                    User newUser = new User();
                    newUser.setName(registerDto.getName());
                    newUser.setSurname(registerDto.getSurname());
                    newUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));
                    newUser.setDni(registerDto.getDni());
                    newUser.setRole(RolesEnum.CLIENT);
                    User newUserSaved = this.userRepository.save(newUser);
                    log.info("Sign Up success - UserID: {}", newUserSaved.getId());
                    this.metrics.getSignupSuccess().increment();
                    return new ResponseDTO<>(true, null, null);
                } catch (Exception e) {
                    log.error("ERROR - {}", e.getMessage());
                    this.metrics.getSignUpFailure().increment();
                    return new ResponseDTO<>(false, e.getMessage(), null);
                } finally {
                    log.debug("Process completed");
                }
            });
        }
    }

    public ResponseDTO<UserDTO> getUserDataToken(String token) {
        try (var ignored = metrics.trace("GET_USER_DATA_TOKEN")) {
            log.info("Start process | token={}", maskToken(token));
            return metrics.getGetUserDataTokenTimer().record(() -> {
                try {
                    // Step 1: Auth token
                    TokenDTO tokenDTO = externalResoursesConnectionService.authenticateToken(token);
                    log.debug("Token authentication result | authenticated={} | userId={}", tokenDTO.isAuthenticated(), tokenDTO.getUserId());
                    // Step 2: Auth validation
                    if (!tokenDTO.isAuthenticated()) {
                        log.info("Token invalid or expired");
                        return new ResponseDTO<>(false, "Token expired or invalid", null);
                    }
                    // Step 3: Get user data on DB
                    Optional<User> userOptional = userRepository.findById(Long.valueOf(tokenDTO.getUserId()));
                    if (userOptional.isEmpty()) {
                        log.info("User not found | userId={}", tokenDTO.getUserId());
                        return new ResponseDTO<>(false, "User not found", null);
                    }
                    // Step 4: DTO conversion & password removal
                    User user = userOptional.get();
                    MDC.put("userId", user.getId().toString());
                    UserDTO userDTO = userMapper.toDto(user);
                    userDTO.setPassword(null);
                    log.info("Success");
                    return new ResponseDTO<>(true, null, userDTO);
                } catch (Exception e) {
                    // Manejo centralizado de errores
                    log.error("Unexpected error | message={}", e.getMessage(), e);
                    return new ResponseDTO<>(false, "Internal server error", null);
                } finally {
                    log.debug("Process completed");
                }
            });
        }
    }

    private String maskToken(String token) {
        if (token == null || token.length() < 10) return "****";
        return token.substring(0, 5) + "*****" + token.substring(token.length() - 3);
    }

}
