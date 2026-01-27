package com.lunanotes.controller.user.docs;

import com.lunanotes.mapper.user.CreateUserDTO;
import com.lunanotes.mapper.user.UpdateUserDTO;
import com.lunanotes.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface AdminUserAPI {

    @Operation(
            summary = "Find User By Id",
            description = "Finding User by UserId, returning Full User information",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Find One Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Result.class),
                                    examples = @ExampleObject(
                                            summary = "Success",
                                            description = "Current user",
                                            value = """
                                                    {
                                                      	"flag": true,
                                                      	"code": 200,
                                                      	"message": "Find One Success",
                                                      	"data": {
                                                      		"id": 1,
                                                      		"username": "test",
                                                      		"email": "test@mail.ru",
                                                      		"roles": "USER",
                                                      		"enabled": false,
                                                      		"telegramId": null,
                                                      		"telegramUsername": null,
                                                      		"createdAt": "2026-01-25T14:49:08.951504",
                                                      		"updatedAt": "2026-01-25T15:07:53.446063"
                                                      	}
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    Result findUserById(@PathVariable String userId);

    @Operation(
            summary = "Find All Users",
            description = "Finding All Users, returning Full User information",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Find All Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Result.class),
                                    examples = @ExampleObject(
                                            summary = "Success",
                                            description = "Current user",
                                            value = """
                                                    {
                                                        "flag": true,
                                                        "code": 200,
                                                        "message": "Find All Success",
                                                        "data": [
                                                            {
                                                                "id": 1,
                                                                "username": "test",
                                                                "email": "test@mail.ru",
                                                                "roles": "USER",
                                                                "enabled": false,
                                                                "telegramId": null,
                                                                "telegramUsername": null,
                                                                "createdAt": "2026-01-25T14:49:08.951504",
                                                                "updatedAt": "2026-01-25T15:07:53.446063"
                                                            },
                                                            {
                                                                "id": 2,
                                                                "username": "test2",
                                                                "email": "test@mail.ru",
                                                                "roles": "ADMIN USER",
                                                                "enabled": true,
                                                                "telegramId": null,
                                                                "telegramUsername": null,
                                                                "createdAt": "2026-01-25T15:09:21.593954",
                                                                "updatedAt": "2026-01-25T15:09:21.593954"
                                                            }
                                                        ]
                                                    }
                                            """
                                    )
                            )
                    )
            }
    )
    Result findAll();

    @Operation(
            summary = "Add User",
            description = "Adding a new User",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User Data",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateUserDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Add Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Result.class),
                                    examples = @ExampleObject(
                                            summary = "Success",
                                            description = "Add Success",
                                            value = """
                                                    {
                                                    	"flag": true,
                                                    	"code": 200,
                                                    	"message": "Add Success",
                                                    	"data": {
                                                    		"id": 2,
                                                    		"username": "test3",
                                                    		"email": "test@mail.ru",
                                                    		"roles": "USER",
                                                    		"enabled": true,
                                                    		"telegramId": null,
                                                    		"telegramUsername": null,
                                                    		"createdAt": "2026-01-25T16:25:32.1970687",
                                                    		"updatedAt": "2026-01-25T16:25:32.1970687"
                                                    	}
                                                    }
                                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Provided arguments are invalid",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Result.class),
                                    examples = @ExampleObject(
                                            summary = "Provided arguments are invalid",
                                            description = "Provided arguments are invalid",
                                            value = """
                                                    {
                                                        "flag": false,
                                                        "code": 400,
                                                        "message": "Provided arguments are invalid",
                                                        "data": {
                                                            "username": "username is required."
                                                        }
                                                    }
                                            """
                                    )
                            )
                    )
            }
    )
    Result addUser(@Valid @RequestBody CreateUserDTO newUser);

    @Operation(
            summary = "Update User",
            description = "Updating a User",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Public User Data",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UpdateUserDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Update Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Result.class),
                                    examples = @ExampleObject(
                                            summary = "Success",
                                            description = "Update Success",
                                            value = """
                                                    {
                                                    	"flag": true,
                                                    	"code": 200,
                                                    	"message": "Update Success",
                                                    	"data": {
                                                    		"id": 2,
                                                    		"username": "tom",
                                                    		"email": "test@mail.ru",
                                                    		"roles": "USER",
                                                    		"enabled": true,
                                                    		"telegramId": null,
                                                    		"telegramUsername": null,
                                                    		"createdAt": "2026-01-25T16:43:50.486433",
                                                    		"updatedAt": "2026-01-25T16:43:56.8009085"
                                                    	}
                                                    }
                                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Provided arguments are invalid",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Result.class),
                                    examples = @ExampleObject(
                                            summary = "Provided arguments are invalid",
                                            description = "Provided arguments are invalid",
                                            value = """
                                                    {
                                                        "flag": false,
                                                        "code": 400,
                                                        "message": "Provided arguments are invalid",
                                                        "data": {
                                                            "username": "username is required."
                                                        }
                                                    }
                                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Result.class),
                                    examples = @ExampleObject(
                                            summary = "User not found",
                                            description = "Could not find user with specified Id",
                                            value = """
                                                    {
                                                    	"flag": false,
                                                    	"code": 404,
                                                    	"message": "Could not find user with Id 256",
                                                    	"data": null
                                                    }
                                            """
                                    )
                            )
                    )
            }
    )
    Result updateUser(@PathVariable String userId, @Valid @RequestBody UpdateUserDTO updateUserDTO);

    @Operation(
            summary = "Delete User",
            description = "Deleting a User",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Delete Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Result.class),
                                    examples = @ExampleObject(
                                            summary = "Success",
                                            description = "Add Success",
                                            value = """
                                                    {
                                                    	"flag": true,
                                                    	"code": 200,
                                                    	"message": "Delete Success",
                                                    	"data": null
                                                    }
                                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Result.class),
                                    examples = @ExampleObject(
                                            summary = "User not found",
                                            description = "Could not find user with specified Id",
                                            value = """
                                                    {
                                                    	"flag": false,
                                                    	"code": 404,
                                                    	"message": "Could not find user with Id 256",
                                                    	"data": null
                                                    }
                                            """
                                    )
                            )
                    )
            }
    )
    Result deleteUser(@PathVariable String userId);

}
