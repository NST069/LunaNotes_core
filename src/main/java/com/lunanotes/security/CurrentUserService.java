package com.lunanotes.security;

import com.lunanotes.exception.ObjectNotFoundException;
import com.lunanotes.mapper.UserPrincipal;
import com.lunanotes.model.User;
import com.lunanotes.repository.UserJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserService {

    @Autowired
    private UserJPARepository userRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserPrincipal) {
            return ((UserPrincipal) principal).getUser();
        } else if (principal instanceof String username) {
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException(
                            "User not found with username: " + username
                    ));
        } else if (principal instanceof Jwt) {
            String userId = ((Jwt) authentication.getPrincipal()).getClaim("userId").toString();
            return userRepository.findById(userId)
                    .orElseThrow(() -> new ObjectNotFoundException("user",  userId));
        }
        else {
            throw new IllegalStateException(
                    "Unexpected principal type: " + principal.getClass().getName()
            );
        }
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }

    public boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);
    }
}
