package com.lunanotes.security;

import com.lunanotes.mapper.UserPrincipal;
import com.lunanotes.mapper.UserToUserDTOConverter;
import com.lunanotes.model.User;
import com.lunanotes.service.UserService;
import com.lunanotes.util.Result;
import com.lunanotes.util.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.endpoint.base-url}/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    private final UserService userService;

    @PostMapping("/login")
    public Result getLoginInfo(Authentication authentication) {
        log.debug("Authenticated user: {}", authentication.getName());
        System.out.println(authentication);
        return new Result(true, HttpStatus.OK.value(), "User Info and JSON Web Token", this.authService.createLoginInfo(authentication));
    }

    @PostMapping("/register")
    public Result registerUser(@Valid @RequestBody RegistrationRequest registrationRequest) {
        log.debug("Register and login request for username: {}", registrationRequest.username());

        if (userService.existsByUsername(registrationRequest.username())) {
            return new Result(false, HttpStatus.CONFLICT.value(),
                    "Username already exists", null);
        }

        User newUser = User.builder()
                .username(registrationRequest.username())
                .password(registrationRequest.password())
                .email(registrationRequest.email())
                .enabled(true)
                .roles(UserRole.USER.name())
                .build();
        User savedUser = userService.save(newUser);

        UserPrincipal userPrincipal = new UserPrincipal(savedUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal,
                null,
                userPrincipal.getAuthorities()
        );

        return new Result(true, HttpStatus.CREATED.value(), "User Registered", this.authService.createLoginInfo(authentication));
    }
}
