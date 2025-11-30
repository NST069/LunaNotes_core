package com.lunanotes.service;

import com.lunanotes.exception.UserNotFoundException;
import com.lunanotes.model.User;
import com.lunanotes.repository.UsersJPARepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UsersDataService {

    @Autowired
    private final UsersJPARepository usersJPARepository;

    public UsersDataService(UsersJPARepository usersJPARepository) {
        this.usersJPARepository = usersJPARepository;
    }

    public User findById(String userId) {
        return this.usersJPARepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public List<User> findAll(){
        return this.usersJPARepository.findAll();
    }

    public User save(User user) {
        return this.usersJPARepository.save(user);
    }

    public User update(String userId, User user) {
        return this.usersJPARepository.findById(userId)
                .map(oldUser -> {
                    oldUser.setUserName(user.getUserName());

                    return this.usersJPARepository.save(oldUser);
                })
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public void delete(String userId) {
        User user = this.usersJPARepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        this.usersJPARepository.deleteById(userId);
    }

}
