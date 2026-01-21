package com.lunanotes.mapper;

public record PublicUserDTO(long id,
                            String username,
                            String email,
                            String telegramUsername,
                            boolean enabled
) {
}
