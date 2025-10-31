package com.billeteraVirtual.users.service;

import com.billeteraVirtual.users.Mapper.UserMapper;
import com.billeteraVirtual.users.dto.*;
import com.billeteraVirtual.users.entity.User;
import com.billeteraVirtual.users.enumerators.RolesEnum;
import com.billeteraVirtual.users.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        UserDTO userDTO = userRepository.findByDni(loginRequestDTO.getDni())
                .map(user -> {
                    boolean authenticated = passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword());
                    if (authenticated) return userMapper.toDto(user);
                    return null;
                })
                .orElseGet(() -> null);
        if (userDTO == null) {
            return new ResponseDTO<>(false, "invalid credentials", null);
        }
        ResponseDTO<?> tokenResponseDTO = externalResoursesConnectionService.generateToken(
                userDTO.getId().toString(),
                userDTO.getDni(),
                userDTO.getRole().id);
        if (!tokenResponseDTO.isSuccess()) {
            log.error("Token Error");
            return new ResponseDTO<>(false, "Error", null);
        }
        return new ResponseDTO<>(true, null, (String) tokenResponseDTO.getData());
    }

    public ResponseDTO<?> registerNewClient(RegisterDTO registerDto) {
        try {
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
        }
    }

    public ResponseDTO<UserDTO> getUserDataToken(String token) {
        TokenDTO tokenDTO = externalResoursesConnectionService.authenticateToken(token);
        if (!tokenDTO.isAuthenticated()) {
            return new ResponseDTO<>(false, "Token expired", null);
        }
        return this.userRepository.findById(Long.valueOf(tokenDTO.getUserId())).map(user -> {
            UserDTO userDTO = userMapper.toDto(user);
            userDTO.setPassword(null);
            return new ResponseDTO<>(true, null, userDTO);
        }).orElseGet(() -> new ResponseDTO<>(false, "User not found", null));
    }

}
