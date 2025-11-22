package com.lunanotes.controller;

import com.lunanotes.mapper.UserDTO;
import com.lunanotes.mapper.UserDTOToUserConverter;
import com.lunanotes.mapper.UserToUserDTOConverter;
import com.lunanotes.model.User;
import com.lunanotes.service.UsersDataService;
import com.lunanotes.util.Result;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UsersController {

    private final UsersDataService usersDataService;

    private final UserToUserDTOConverter userToUserDTOConverter;

    private final UserDTOToUserConverter userDTOToUserConverter;


    public UsersController(UsersDataService usersDataService, UserToUserDTOConverter userToUserDTOConverter, UserDTOToUserConverter userDTOToUserConverter) {
        this.usersDataService = usersDataService;
        this.userToUserDTOConverter = userToUserDTOConverter;
        this.userDTOToUserConverter = userDTOToUserConverter;
    }

    @GetMapping("/{userId}")
    public Result findUserById(@PathVariable String userId) {
        User foundUser = this.usersDataService.findById(userId);
        UserDTO foundUserDTO = this.userToUserDTOConverter.convert(foundUser);
        return new Result(true, HttpStatus.OK.value(), "Find One Success", foundUserDTO);
    }

    @PostMapping
    public Result addUser(@Valid @RequestBody UserDTO userDTO) {
        User newUser = this.userDTOToUserConverter.convert(userDTO);
        User savedUser = this.usersDataService.save(newUser);
        UserDTO savedUserDTO = this.userToUserDTOConverter.convert(savedUser);
        return new Result(true, HttpStatus.OK.value(), "Add Success", savedUserDTO);
    }

    @PutMapping("/{userId}")
    public Result updateUser(@PathVariable String userId, @Valid @RequestBody UserDTO userDTO) {
        User update = this.userDTOToUserConverter.convert(userDTO);
        User updatedUser = this.usersDataService.update(userId, update);
        UserDTO updatedUserDTO = this.userToUserDTOConverter.convert(updatedUser);
        return new Result(true, HttpStatus.OK.value(), "Update Success", updatedUserDTO);
    }

    @DeleteMapping("/{userId}")
    public Result deleteUser(@PathVariable String userId) {
        this.usersDataService.delete(userId);
        return new Result(true, HttpStatus.OK.value(), "Delete Success", null);
    }
}
