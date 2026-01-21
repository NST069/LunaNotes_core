package com.lunanotes.controller;

import com.lunanotes.mapper.PublicUserDTO;
import com.lunanotes.mapper.UserToPublicUserDTOConverter;
import com.lunanotes.model.User;
import com.lunanotes.service.UserService;
import com.lunanotes.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.endpoint.base-url}/users")
public class PublicUserController {

    private final UserService userService;

    private final UserToPublicUserDTOConverter userToPublicUserDTOConverter;

    @GetMapping("/{userId}")
    public Result findUserById(@PathVariable String userId) {
        User foundUser = this.userService.findById(userId);
        PublicUserDTO foundUserDTO = this.userToPublicUserDTOConverter.convert(foundUser);
        return new Result(true, HttpStatus.OK.value(), "Find One Success", foundUserDTO);
    }


}
