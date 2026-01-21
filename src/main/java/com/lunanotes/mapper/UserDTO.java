package com.lunanotes.mapper;

import java.time.LocalDateTime;

public record UserDTO(long id,
                      String username,
                      String email,
                      String roles,
                      boolean enabled,
                      String telegramId,
                      String telegramUsername,
                      LocalDateTime createdAt,
                      LocalDateTime updatedAt) {
}
