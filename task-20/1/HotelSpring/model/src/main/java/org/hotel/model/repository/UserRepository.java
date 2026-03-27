package org.hotel.model.repository;

import org.hotel.model.entities.User;

import java.util.Optional;

public interface UserRepository {
    void save(User user);
    Optional<User> findByUsername(String username);
}
