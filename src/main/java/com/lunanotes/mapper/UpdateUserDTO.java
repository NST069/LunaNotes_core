package com.lunanotes.mapper;

import jakarta.validation.constraints.NotEmpty;

public record UpdateUserDTO(long id,
                            @NotEmpty(message = "username is required.")
                            String username,
                            String email,
                            @NotEmpty(message = "roles are required.")
                            String roles,
                            boolean enabled,
                            String telegramId,
                            String telegramUsername) {
}
