package com.slotify.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.slotify.model.User;


public interface UserRepository extends JpaRepository<User, Long> { // Declare methods; Spring implements them. This repo manages User, ID type is long
    Optional<User> findByEmail(String email); // Result may be empty if email not found, spring created a query from the method name
    boolean existsByEmail(String email); // Usefull for registration checks
}