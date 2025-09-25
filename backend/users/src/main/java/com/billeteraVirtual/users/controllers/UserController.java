package com.billeteraVirtual.users.controllers;


import com.billeteraVirtual.users.dto.ResponseDTO;
import com.billeteraVirtual.users.dto.UserCredentialsRequestDTO;
import com.billeteraVirtual.users.dto.UserCredentialsResponseDTO;
import com.billeteraVirtual.users.dto.UserDTO;
import com.billeteraVirtual.users.service.UserService;
import com.billeteraVirtual.users.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/getUserData/{user_id}")
    public ResponseDTO<UserDTO> getUserData(@PathVariable Long user_id) {
        long startTime = System.currentTimeMillis();

        ResponseDTO<UserDTO> responseDTO = this.userService.getUserData(user_id);

        Utils.waitRandomMiliSeconds(2000, 5000);
        log.info("GET_USER_DATA", kv("duration_ms", Utils.calculateDuration(startTime)));
        return responseDTO;
    }

    @PostMapping("/createUser")
    public ResponseDTO<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        long startTime = System.currentTimeMillis();

        ResponseDTO<UserDTO> responseDTO = this.userService.createUser(userDTO);

        Utils.waitRandomMiliSeconds(2000, 5000);
        log.info("CREATE_USER", kv("duration_ms", Utils.calculateDuration(startTime)));
        return responseDTO;
    }

    @PostMapping("/validateUserCredentials")
    public UserCredentialsResponseDTO validateUserCredentials(@RequestBody UserCredentialsRequestDTO userCredentialsRequestDTO) {
        long startTime = System.currentTimeMillis();

        UserCredentialsResponseDTO userCredentialsResponseDTO = this.userService.validateUserCredentials(userCredentialsRequestDTO);

        Utils.waitRandomMiliSeconds(2000, 5000);
        log.info("CREDENTIALS_VALIDATION", kv("duration_ms", Utils.calculateDuration(startTime)));
        return userCredentialsResponseDTO;
    }

}
