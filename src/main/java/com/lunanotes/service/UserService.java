package com.lunanotes.service;

import com.lunanotes.exception.ObjectNotFoundException;
import com.lunanotes.mapper.user.UserPrincipal;
import com.lunanotes.model.User;
import com.lunanotes.repository.UserJPARepository;
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
public class UserService implements UserDetailsService {

    private final UserJPARepository userJPARepository;

    private final PasswordEncoder passwordEncoder;

    public User findById(String userId) {
        return this.userJPARepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user", userId));
    }

    public List<User> findAll(){
        return this.userJPARepository.findAll();
    }

    public User save(User user) {
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        return this.userJPARepository.save(user);
    }

    public User update(String userId, User user) {
        return this.userJPARepository.findById(userId)
                .map(oldUser -> {
                    oldUser.setUsername(user.getUsername());
                    oldUser.setRoles(user.getRoles());

                    return this.userJPARepository.save(oldUser);
                })
                .orElseThrow(() -> new ObjectNotFoundException("user", userId));
    }

    public void delete(String userId) {
        User user = this.userJPARepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user", userId));
        this.userJPARepository.deleteById(userId);
    }

    public void inactivate(String userId) {
        User user = this.userJPARepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user", userId));
        user.setEnabled(false);
        this.userJPARepository.save(user);
    }

    public void activate(String userId) {
        User user = this.userJPARepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user", userId));
        user.setEnabled(true);
        this.userJPARepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return this.userJPARepository.findByUsername(username).isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userJPARepository.findByUsername(username)
                .map(UserPrincipal::new)
                .orElseThrow(()->new UsernameNotFoundException("username "+username+" not found"));
    }
}
