package com.lunanotes.security;

import com.lunanotes.mapper.user.UserDTO;
import com.lunanotes.mapper.user.UserPrincipal;
import com.lunanotes.mapper.user.UserToUserDTOConverter;
import com.lunanotes.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;

    private final UserToUserDTOConverter userToUserDTOConverter;

    public Map<String, Object> createLoginInfo(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal)authentication.getPrincipal();
        User user = userPrincipal.getUser();
        UserDTO userDTO = this.userToUserDTOConverter.convert(user);

        String token = this.jwtProvider.createToken(authentication);
        Map<String, Object> loginResultMap = new HashMap<>();
        loginResultMap.put("userInfo", userDTO);
        loginResultMap.put("token", token);

        return loginResultMap;
    }
}
