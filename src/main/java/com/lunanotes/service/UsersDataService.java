package com.lunanotes.service;

import com.lunanotes.repository.UsersCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class UsersDataService {

    @Autowired
    private UsersCrudRepository usersCrudRepository;
}
