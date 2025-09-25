package com.billeteraVirtual.users.service;

import com.billeteraVirtual.users.Mapper.UserMapper;
import com.billeteraVirtual.users.dto.ResponseDTO;
import com.billeteraVirtual.users.dto.UserCredentialsRequestDTO;
import com.billeteraVirtual.users.dto.UserCredentialsResponseDTO;
import com.billeteraVirtual.users.dto.UserDTO;
import com.billeteraVirtual.users.entity.User;
import com.billeteraVirtual.users.enumerators.RolesEnum;
import com.billeteraVirtual.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserMapper userMapper;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userMapper = mock(UserMapper.class);
        userService = new UserService(userRepository, passwordEncoder, userMapper);
    }

    @Test
    void getUserData_shouldReturnUserDTO_whenUserExists() {
        // given
        User user = new User();
        user.setId(1L);
        user.setName("Juan");
        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setName("Juan");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        // when
        ResponseDTO<UserDTO> response = userService.getUserData(1L);

        // then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isEqualTo(dto);
        assertThat(response.getErrorMsg()).isNull();
    }

    @Test
    void getUserData_shouldReturnError_whenUserNotFound() {
        // given
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // when
        ResponseDTO<UserDTO> response = userService.getUserData(99L);

        // then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getData()).isNull();
        assertThat(response.getErrorMsg()).isEqualTo("User not found");
    }

    @Test
    void createUser_shouldReturnSuccessResponse_whenUserIsSaved() {
        // given
        UserDTO dto = new UserDTO();
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
        when(userMapper.toDto(savedUser)).thenReturn(savedUserDTO);


        ResponseDTO<UserDTO> response = userService.createUser(dto);

        // then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getErrorMsg()).isNull();
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getId()).isEqualTo(10L);
        assertThat(response.getData().getPassword()).isNull(); // la password debe ser null en el DTO devuelto

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
    void createUser_shouldReturnErrorResponse_whenRepositoryThrowsException() {
        // given
        UserDTO dto = new UserDTO();
        dto.setName("Ana");
        dto.setSurname("García");
        dto.setPassword("plainPass");
        dto.setDni("12345678");

        when(passwordEncoder.encode("plainPass")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("DB error"));

        // when
        ResponseDTO<UserDTO> response = userService.createUser(dto);

        // then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getData()).isNull();
        assertThat(response.getErrorMsg()).isEqualTo("DB error");
    }

    @Test
    void validateUserCredentials_shouldReturnAuthenticatedTrue_whenPasswordMatches() {
        // given
        User user = new User();
        user.setId(1L);
        user.setPassword("encodedPass");
        user.setDni("12345678");

        UserDTO dto = new UserDTO();
        dto.setId(1L);

        UserCredentialsRequestDTO request = new UserCredentialsRequestDTO("12345678", "plainPass");

        when(userRepository.findByDni("12345678")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plainPass", "encodedPass")).thenReturn(true);
        when(userMapper.toDto(user)).thenReturn(dto);

        // when
        UserCredentialsResponseDTO response = userService.validateUserCredentials(request);

        // then
        assertThat(response.isAuthenticated()).isTrue();
        assertThat(response.getUserDTO()).isEqualTo(dto);
    }

    @Test
    void validateUserCredentials_shouldReturnAuthenticatedFalse_whenPasswordDoesNotMatch() {
        // given
        User user = new User();
        user.setPassword("encodedPass");
        user.setDni("12345678");

        UserCredentialsRequestDTO request = new UserCredentialsRequestDTO("12345678", "wrongPass");

        when(userRepository.findByDni("12345678")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "encodedPass")).thenReturn(false);

        // when
        UserCredentialsResponseDTO response = userService.validateUserCredentials(request);

        // then
        assertThat(response.isAuthenticated()).isFalse();
        assertThat(response.getUserDTO()).isNull();
    }

    @Test
    void validateUserCredentials_shouldReturnAuthenticatedFalse_whenUserNotFound() {
        // given
        UserCredentialsRequestDTO request = new UserCredentialsRequestDTO("99999999", "anyPass");
        when(userRepository.findByDni("99999999")).thenReturn(Optional.empty());

        // when
        UserCredentialsResponseDTO response = userService.validateUserCredentials(request);

        // then
        assertThat(response.isAuthenticated()).isFalse();
        assertThat(response.getUserDTO()).isNull();
    }


}