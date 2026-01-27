package com.lunanotes.mapper.user;

import jakarta.validation.constraints.NotEmpty;

public record CreateUserDTO(@NotEmpty(message = "username is required.")
                            String username,
                            @NotEmpty(message = "password is required.")
                            String password,
                            String email,
                            String telegramId,
                            String telegramUsername) {
}