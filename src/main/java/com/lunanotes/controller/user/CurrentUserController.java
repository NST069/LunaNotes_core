package com.lunanotes.controller.user;

import com.lunanotes.controller.user.docs.CurrentUserAPI;
import com.lunanotes.mapper.user.UpdateUserDTO;
import com.lunanotes.mapper.user.UpdateUserDTOToUserConverter;
import com.lunanotes.mapper.user.UserDTO;
import com.lunanotes.mapper.user.UserToUserDTOConverter;
import com.lunanotes.model.User;
import com.lunanotes.security.CurrentUserService;
import com.lunanotes.service.UserService;
import com.lunanotes.util.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.endpoint.base-url}/users/me")
public class CurrentUserController implements CurrentUserAPI {

    private final CurrentUserService currentUserService;

    private final UserService userService;

    private final UserToUserDTOConverter userToUserDTOConverter;

    private final UpdateUserDTOToUserConverter updateUserDTOToUserConverter;

    @GetMapping
    public Result getCurrentUser() {
        User currentUser = this.currentUserService.getCurrentUser();
        UserDTO currentUserDTO = this.userToUserDTOConverter.convert(currentUser);
        return new Result(true, HttpStatus.OK.value(), "Current user", currentUserDTO);
    }

    @PutMapping
    public Result updateCurrentUser(@Valid @RequestBody UpdateUserDTO userDTO) {
        User currentUser = this.currentUserService.getCurrentUser();

        User update = this.updateUserDTOToUserConverter.convert(userDTO);
        User updatedUser = this.userService.update(currentUser.getId().toString(), update);
        UserDTO updatedUserDTO = this.userToUserDTOConverter.convert(updatedUser);
        return new Result(true, HttpStatus.OK.value(), "Update Success", updatedUserDTO);
    }

    @DeleteMapping
    @Transactional
    public Result deleteCurrentUser() {
        User currentUser = this.currentUserService.getCurrentUser();

        this.userService.inactivate(currentUser.getId().toString());
        return new Result(true, HttpStatus.OK.value(), "Account inactivated", null);
    }

}
