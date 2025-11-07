package com.billeteraVirtual.users.controllers;


import com.billeteraVirtual.users.dto.LoginRequestDTO;
import com.billeteraVirtual.users.dto.RegisterDTO;
import com.billeteraVirtual.users.dto.ResponseDTO;
import com.billeteraVirtual.users.dto.UserDTO;
import com.billeteraVirtual.users.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
        ResponseDTO<String> responseDTO = this.userService.login(loginRequestDTO);
        return responseDTO;
    }

    @PostMapping("/signup")
    public ResponseDTO<?> signup(@RequestBody RegisterDTO registerDto) {
        ResponseDTO<?> responseDTO = this.userService.signup(registerDto);
        return responseDTO;
    }

    @GetMapping("/getUserDataToken/{token}")
    public ResponseDTO<UserDTO> getUserDataToken(@PathVariable String token) {
        ResponseDTO<UserDTO> responseDTO = this.userService.getUserDataToken(token);
        return responseDTO;
    }

}
