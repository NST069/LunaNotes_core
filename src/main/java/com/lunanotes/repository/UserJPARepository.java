package com.lunanotes.repository;

import com.lunanotes.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJPARepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);
}
