package org.hotel.app.security;

import org.hotel.model.entities.User;
import org.hotel.model.enums.Role;
import org.hotel.model.exceptions.UserAlreadyExistsException;
import org.hotel.model.exceptions.UserNotFoundException;
import org.hotel.model.exceptions.WrongPasswordException;
import org.hotel.model.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void register_ShouldSaveNewUser_WhenAllCorrect() {
        String username = "user";
        String password = "user";
        Role role = Role.ROLE_USER;

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        when(passwordEncoder.encode(password)).thenReturn("encoded_password");

        when(jwtService.generateToken(any(User.class))).thenReturn("token");

        String actual = authenticationService.register(username, password, role);

        assertNotNull(actual);
        assertEquals("token", actual);

        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).encode(password);
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtService, times(1)).generateToken(any(User.class));
    }

    @Test
    void register_ShouldThrowUserAlreadyExistsException_WhenUserAlreadyExists() {
        String username = "user";
        String password = "user";
        Role role = Role.ROLE_USER;
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> authenticationService.register(username, password, role));

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void authenticate_ShouldGenerateUsersToken_WhenAllCorrect() {
        String username = "user";
        String password = "user";
        Role role = Role.ROLE_USER;
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        when(jwtService.generateToken(user)).thenReturn("token");

        String actual = authenticationService.authenticate(username, password);

        assertEquals("token", actual);

        verify(userRepository, times(1)).findByUsername(username);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateToken(user);
    }

    @Test
    void authenticate_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        String username = "user";
        String password = "user";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authenticationService.authenticate(username, password));

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void authenticate_ShouldThrowWrongPasswordException_WhenPasswordIsIncorrect() {
        String username = "user";
        String wrongPassword = "user";

        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(WrongPasswordException.class, () -> authenticationService.authenticate(username, wrongPassword));

        verify(userRepository, times(1)).findByUsername(username);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
