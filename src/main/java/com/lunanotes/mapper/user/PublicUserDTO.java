package com.lunanotes.mapper.user;

public record PublicUserDTO(long id,
                            String username,
                            String email,
                            String telegramUsername,
                            boolean enabled
) {
}
