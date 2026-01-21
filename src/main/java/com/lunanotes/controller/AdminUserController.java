package com.lunanotes.controller;

import com.lunanotes.mapper.*;
import com.lunanotes.model.User;
import com.lunanotes.service.UserService;
import com.lunanotes.util.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.endpoint.base-url}/admin/users")
public class AdminUserController {

    private final UserService userService;

    private final UserToUserDTOConverter userToUserDTOConverter;

    private final UpdateUserDTOToUserConverter updateUserDTOToUserConverter;

    @GetMapping("/{userId}")
    public Result findUserById(@PathVariable String userId) {
        User foundUser = this.userService.findById(userId);
        UserDTO foundUserDTO = this.userToUserDTOConverter.convert(foundUser);
        return new Result(true, HttpStatus.OK.value(), "Find One Success", foundUserDTO);
    }

    @GetMapping
    public Result findAll(){
        List<User> users = this.userService.findAll();
        List<UserDTO> usersDTO = users.stream().map(userToUserDTOConverter::convert).toList();
        return new Result(true, HttpStatus.OK.value(), "Find All Success", usersDTO);
    }

    @PostMapping
    public Result addUser(@Valid @RequestBody User newUser) {
        User savedUser = this.userService.save(newUser);
        UserDTO savedUserDTO = this.userToUserDTOConverter.convert(savedUser);
        return new Result(true, HttpStatus.OK.value(), "Add Success", savedUserDTO);
    }

    @PutMapping("/{userId}")
    public Result updateUser(@PathVariable String userId, @Valid @RequestBody UpdateUserDTO updateUserDTO) {
        User update = this.updateUserDTOToUserConverter.convert(updateUserDTO);
        User updatedUser = this.userService.update(userId, update);
        UserDTO updatedUserDTO = this.userToUserDTOConverter.convert(updatedUser);
        return new Result(true, HttpStatus.OK.value(), "Update Success", updatedUserDTO);
    }

    @DeleteMapping("/{userId}")
    public Result deleteUser(@PathVariable String userId) {
        this.userService.delete(userId);
        return new Result(true, HttpStatus.OK.value(), "Delete Success", null);
    }
}
