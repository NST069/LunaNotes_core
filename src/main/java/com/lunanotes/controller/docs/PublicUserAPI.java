package com.lunanotes.controller.docs;

import com.lunanotes.mapper.PublicUserDTO;
import com.lunanotes.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.PathVariable;

public interface PublicUserAPI {

    @Operation(
            summary = "Find User By Id",
            description = "Finding User by UserId, returning Public User information",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Public User Data",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = PublicUserDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Find One Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Result.class),
                                    examples = @ExampleObject(
                                            summary = "Success",
                                            description = "Find one Success",
                                            value = """
                                                    {
                                                     	"flag": true,
                                                     	"code": 200,
                                                     	"message": "Find One Success",
                                                     	"data": {
                                                     		"id": 1,
                                                     		"username": "test",
                                                     		"email": "test@mail.ru",
                                                     		"telegramUsername": null,
                                                     		"enabled": true
                                                     	}
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Could not find user with provided Id",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Result.class),
                                    examples = @ExampleObject(
                                            summary = "User not found",
                                            description = "Could not find user with provided Id",
                                            value = """
                                                    {
                                                    	"flag": false,
                                                    	"code": 404,
                                                    	"message": "Could not find user with Id 666",
                                                    	"data": null
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    Result findUserById(@PathVariable String userId);
}
