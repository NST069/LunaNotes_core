package com.lunanotes.controller.docs;

import com.lunanotes.mapper.UpdateUserDTO;
import com.lunanotes.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

public interface CurrentUserAPI {

    @Operation(
            summary = "Get Current User",
            description = "Getting Current User, returning Full User information",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Current user",
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
                                                     	"message": "Current user",
                                                     	"data": {
                                                     		"id": 1,
                                                     		"username": "test",
                                                     		"email": "test@mail.ru",
                                                     		"roles": "USER",
                                                     		"enabled": true,
                                                     		"telegramId": null,
                                                     		"telegramUsername": null,
                                                     		"createdAt": "2026-01-25T14:49:08.951504",
                                                     		"updatedAt": "2026-01-25T14:49:08.951504"
                                                     	}
                                                     }
                                                    """
                                    )
                            )
                    )
            }
    )
    Result getCurrentUser();

    @Operation(
            summary = "Update Current User",
            description = "Updating current User",
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
                                              		"id": 1,
                                              		"username": "test",
                                              		"email": "test@mail.ru",
                                              		"roles": "USER",
                                              		"enabled": true,
                                              		"telegramId": null,
                                              		"telegramUsername": null,
                                              		"createdAt": "2026-01-25T14:49:08.951504",
                                              		"updatedAt": "2026-01-25T14:49:08.951504"
                                              	}
                                            }
                                            """
                                    )
                            )
                    )
            }
    )
    Result updateCurrentUser(@Valid @RequestBody UpdateUserDTO userDTO);

    @Operation(
            summary = "Delete User",
            description = "Deleting a User",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Account inactivated",
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
                                                        "message": "Account inactivated",
                                                        "data": null
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    Result deleteCurrentUser();
}
