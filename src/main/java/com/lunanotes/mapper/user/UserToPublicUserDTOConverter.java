package com.lunanotes.mapper.user;

import com.lunanotes.model.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserToPublicUserDTOConverter implements Converter<User, PublicUserDTO> {
    @Override
    public PublicUserDTO convert(User source) {
        return new PublicUserDTO(source.getId(),
                source.getUsername(),
                source.getEmail(),
                source.getTelegramUsername(),
                source.isEnabled());
    }
}
