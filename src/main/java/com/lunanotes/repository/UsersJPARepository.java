package com.lunanotes.repository;

import com.lunanotes.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersJPARepository extends JpaRepository<User, String> {

}
