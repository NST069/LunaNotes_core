package com.lunanotes.service;

import com.lunanotes.exception.ObjectNotFoundException;
import com.lunanotes.mapper.UserPrincipal;
import com.lunanotes.model.User;
import com.lunanotes.repository.UsersJPARepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UsersDataService implements UserDetailsService {

    private final UsersJPARepository usersJPARepository;

    private final PasswordEncoder passwordEncoder;

    public User findById(String userId) {
        return this.usersJPARepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user", userId));
    }

    public List<User> findAll(){
        return this.usersJPARepository.findAll();
    }

    public User save(User user) {
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        return this.usersJPARepository.save(user);
    }

    public User update(String userId, User user) {
        return this.usersJPARepository.findById(userId)
                .map(oldUser -> {
                    oldUser.setUsername(user.getUsername());
                    oldUser.setRoles(user.getRoles());

                    return this.usersJPARepository.save(oldUser);
                })
                .orElseThrow(() -> new ObjectNotFoundException("user", userId));
    }

    public void delete(String userId) {
        User user = this.usersJPARepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user", userId));
        this.usersJPARepository.deleteById(userId);
    }

    public boolean existsByUsername(String username) {
        return this.usersJPARepository.findByUsername(username).isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.usersJPARepository.findByUsername(username)
                .map(UserPrincipal::new)
                .orElseThrow(()->new UsernameNotFoundException("username "+username+" not found"));
    }
}
