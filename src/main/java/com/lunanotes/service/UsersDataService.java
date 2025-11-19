package com.lunanotes.service;

import com.lunanotes.repository.UsersJPARepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UsersDataService {

    @Autowired
    private final UsersJPARepository usersJPARepository;

    public UsersDataService(UsersJPARepository usersJPARepository) {
        this.usersJPARepository = usersJPARepository;
    }
}
