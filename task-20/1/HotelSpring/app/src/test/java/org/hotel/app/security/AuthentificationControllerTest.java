package org.hotel.app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hotel.controller.GlobalExceptionHandler;
import org.hotel.model.enums.Role;
import org.hotel.model.exceptions.UserAlreadyExistsException;
import org.hotel.model.exceptions.UserNotFoundException;
import org.hotel.model.exceptions.WrongPasswordException;
import org.hotel.model.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class AuthentificationControllerTest {

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void register_ShouldReturn200AndToken_WhenRequestIsCorrect() throws Exception {
        AuthenticationController.RegisterRequest request = new AuthenticationController.RegisterRequest("newAdmin", "newAdmin", Role.ROLE_ADMIN);

        when(authenticationService.register("newAdmin", "newAdmin", Role.ROLE_ADMIN)).thenReturn("token");

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/hotel/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"));
        verify(authenticationService, times(1)).register("newAdmin", "newAdmin", Role.ROLE_ADMIN);
    }

    @Test
    void register_ShouldReturn409_WhenUserNameAlreadyExists() throws Exception {
        AuthenticationController.RegisterRequest request = new AuthenticationController.RegisterRequest("newAdmin", "newAdmin", Role.ROLE_ADMIN);

        doThrow(new UserAlreadyExistsException()).when(authenticationService).register("newAdmin", "newAdmin", Role.ROLE_ADMIN);

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/hotel/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
        verify(authenticationService, times(1)).register("newAdmin", "newAdmin", Role.ROLE_ADMIN);
    }

    @Test
    void authenticate_ShouldReturn200AndToken_WhenAllCorrect() throws Exception {
        AuthenticationController.AuthRequest authRequest = new AuthenticationController.AuthRequest("user", "user");

        when(authenticationService.authenticate("user", "user")).thenReturn("token");

        String json = objectMapper.writeValueAsString(authRequest);

        mockMvc.perform(post("/hotel/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"));
        verify(authenticationService, times(1)).authenticate("user", "user");
    }

    @Test
    void authenticate_ShouldReturn401_WhenWrongPassword() throws Exception {
        AuthenticationController.AuthRequest authRequest = new AuthenticationController.AuthRequest("user", "user");

        doThrow(new WrongPasswordException()).when(authenticationService).authenticate("user", "user");

        String json = objectMapper.writeValueAsString(authRequest);

        mockMvc.perform(post("/hotel/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
        verify(authenticationService, times(1)).authenticate("user", "user");
    }

    @Test
    void authenticate_ShouldReturn401_WhenWrongUsername() throws Exception {
        AuthenticationController.AuthRequest authRequest = new AuthenticationController.AuthRequest("user", "user");

        doThrow(new UserNotFoundException()).when(authenticationService).authenticate("user", "user");

        String json = objectMapper.writeValueAsString(authRequest);

        mockMvc.perform(post("/hotel/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
        verify(authenticationService, times(1)).authenticate("user", "user");
    }
}
