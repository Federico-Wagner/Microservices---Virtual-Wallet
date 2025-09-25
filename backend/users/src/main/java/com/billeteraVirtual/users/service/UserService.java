package com.billeteraVirtual.users.service;


import com.billeteraVirtual.users.Mapper.UserMapper;
import com.billeteraVirtual.users.dto.ResponseDTO;
import com.billeteraVirtual.users.dto.UserCredentialsRequestDTO;
import com.billeteraVirtual.users.dto.UserCredentialsResponseDTO;
import com.billeteraVirtual.users.dto.UserDTO;
import com.billeteraVirtual.users.entity.User;
import com.billeteraVirtual.users.enumerators.RolesEnum;
import com.billeteraVirtual.users.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }


    public ResponseDTO<UserDTO> getUserData(Long userId) {
        return this.userRepository.findById(userId).map(user -> {
                    UserDTO userDTO = userMapper.toDto(user);
                    return new ResponseDTO<UserDTO>(true, null, userDTO);
                }).orElseGet(() -> new ResponseDTO<UserDTO>(false, "User not found", null));
    }

    public ResponseDTO<UserDTO> createUser(UserDTO userDTO) {
        try {
            User newUser = new User();
            newUser.setName(userDTO.getName());
            newUser.setSurname(userDTO.getSurname());
            newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            newUser.setDni(userDTO.getDni());
            newUser.setRole(RolesEnum.CLIENT);

            User newUserSaved = this.userRepository.save(newUser);

            UserDTO userSavedDTO = userMapper.toDto(newUserSaved);
            userSavedDTO.setPassword(null);

            return new ResponseDTO<UserDTO>(true, null, userSavedDTO);
        } catch (Exception e){
            log.error("ERROR - {}", e.getMessage());
            return new ResponseDTO<UserDTO>(false, e.getMessage(), null);
        }
    }


    public UserCredentialsResponseDTO validateUserCredentials(UserCredentialsRequestDTO request) {
        return userRepository.findByDni(request.getDni())
                .map(user -> {
                    boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
                    return new UserCredentialsResponseDTO(authenticated, userMapper.toDto(user));
                })
                .orElseGet(() -> new UserCredentialsResponseDTO(false, null));
    }

}
