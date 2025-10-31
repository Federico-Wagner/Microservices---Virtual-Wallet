package com.billeteraVirtual.users.controllers;


import com.billeteraVirtual.users.dto.LoginRequestDTO;
import com.billeteraVirtual.users.dto.RegisterDTO;
import com.billeteraVirtual.users.dto.ResponseDTO;
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

    @GetMapping("/test")
    public String test() {
        return "MS Users works!";
    }

    @PostMapping("/login")
    public ResponseDTO<String> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        long startTime = System.currentTimeMillis();
        ResponseDTO<String> responseDTO = this.userService.login(loginRequestDTO);
//        Utils.waitRandomMiliSeconds(1000, 2000);
        log.info("LOGIN_USER", kv("duration_ms", Utils.calculateDuration(startTime)));
        return responseDTO;
    }

    @PostMapping("/register")
    public ResponseDTO<?> register(@RequestBody RegisterDTO registerDto) {
        long startTime = System.currentTimeMillis();
        ResponseDTO<?> responseDTO = this.userService.registerNewClient(registerDto);
//        Utils.waitRandomMiliSeconds(1000, 2000);
        log.info("REGISTER_USER", kv("duration_ms", Utils.calculateDuration(startTime)));
        return responseDTO;
    }

    @GetMapping("/userData/{token}")
    public ResponseDTO<UserDTO> getUserDataToken(@PathVariable String token) {
        long startTime = System.currentTimeMillis();
        ResponseDTO<UserDTO> responseDTO = this.userService.getUserDataToken(token);
//        Utils.waitRandomMiliSeconds(1000, 2000);
        log.info("GET_USER_DATA_TOKEN", kv("duration_ms", Utils.calculateDuration(startTime)));
        return responseDTO;
    }

}
