package com.lunanotes.mapper;

import com.lunanotes.model.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserToUserDTOConverter implements Converter<User, UserDTO> {
    @Override
    public UserDTO convert(User source) {
        return new UserDTO(source.getId(),
                source.getUsername(),
                source.getEmail(),
                source.getRoles(),
                source.isEnabled(),
                source.getTelegramId(),
                source.getTelegramUsername(),
                source.getCreatedAt(),
                source.getUpdatedAt());
    }
}
