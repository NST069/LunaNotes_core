package com.lunanotes.repository;

import com.lunanotes.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersCrudRepository extends CrudRepository<User, Long> {

}
