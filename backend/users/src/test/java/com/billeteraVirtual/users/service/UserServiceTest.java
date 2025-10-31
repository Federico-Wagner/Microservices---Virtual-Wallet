package com.billeteraVirtual.users.service;

import com.billeteraVirtual.users.Mapper.UserMapper;
import com.billeteraVirtual.users.dto.RegisterDTO;
import com.billeteraVirtual.users.dto.ResponseDTO;
import com.billeteraVirtual.users.dto.UserDTO;
import com.billeteraVirtual.users.entity.User;
import com.billeteraVirtual.users.enumerators.RolesEnum;
import com.billeteraVirtual.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private ExternalResoursesConnectionService externalResoursesConnectionService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserMapper userMapper;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userMapper = mock(UserMapper.class);
        externalResoursesConnectionService = mock(ExternalResoursesConnectionService.class);
        userService = new UserService(externalResoursesConnectionService, userRepository, passwordEncoder, userMapper);
    }

    @Test
    void registerNewClientIsSaved() {
        // given
        RegisterDTO dto = new RegisterDTO();
        dto.setName("Ana");
        dto.setSurname("García");
        dto.setPassword("plainPass");
        dto.setDni("12345678");

        User savedUser = new User();
        savedUser.setId(10L);
        savedUser.setName("Ana");
        savedUser.setSurname("García");
        savedUser.setPassword("encodedPass");
        savedUser.setDni("12345678");
        savedUser.setRole(RolesEnum.CLIENT);

        UserDTO savedUserDTO = new UserDTO();
        savedUserDTO.setId(10L);
        savedUserDTO.setName("Ana");
        savedUserDTO.setSurname("García");
        savedUserDTO.setDni("12345678");

        when(passwordEncoder.encode("plainPass")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        ResponseDTO<?> response = userService.registerNewClient(dto);

        // then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getErrorMsg()).isNull();
        assertThat(response.getData()).isNull();

        // capturamos el User que se guardó
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User captured = captor.getValue();

        assertThat(captured.getName()).isEqualTo("Ana");
        assertThat(captured.getSurname()).isEqualTo("García");
        assertThat(captured.getPassword()).isEqualTo("encodedPass");
        assertThat(captured.getDni()).isEqualTo("12345678");
        assertThat(captured.getRole()).isEqualTo(RolesEnum.CLIENT);
    }

    @Test
    void register_NewClient_shouldReturnErrorResponse_whenRepositoryThrowsException() {
        // given
        RegisterDTO dto = new RegisterDTO();
        dto.setName("Ana");
        dto.setSurname("García");
        dto.setPassword("plainPass");
        dto.setDni("12345678");

        when(passwordEncoder.encode("plainPass")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("DB error"));

        // when
        ResponseDTO<?> response = userService.registerNewClient(dto);

        // then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getData()).isNull();
        assertThat(response.getErrorMsg()).isEqualTo("DB error");
    }

}