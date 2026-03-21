package org.hotel.app.security;

import org.hotel.model.entities.User;
import org.hotel.model.enums.Role;
import org.hotel.model.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {

    /**
     * Репозиторий для работы с таблицей пользователей в БД.
     */
    private final UserRepository userRepository;

    /**
     * Кодировщик паролей.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Сервис для работы с токенами.
     */
    private final JwtService jwtService;

    /**
     * Менеджер аутентификации.
     */
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(final UserRepository userRepositoryP,
                                 final PasswordEncoder passwordEncoderP,
                                 final JwtService jwtServiceP,
                                 final AuthenticationManager authenticationManagerP) {
        this.userRepository = userRepositoryP;
        this.passwordEncoder = passwordEncoderP;
        this.jwtService = jwtServiceP;
        this.authenticationManager = authenticationManagerP;
    }

    @Transactional
    public String register(String username, String password, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);

        userRepository.save(user);

        return jwtService.generateToken(user);
    }

    @Transactional(readOnly = true)
    public String authenticate(String username, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        User user = userRepository.findByUsername(username).orElseThrow();
        return jwtService.generateToken(user);
    }
}
