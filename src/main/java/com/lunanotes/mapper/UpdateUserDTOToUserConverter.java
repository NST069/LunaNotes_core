package com.lunanotes.mapper;

import com.lunanotes.model.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UpdateUserDTOToUserConverter implements Converter<UpdateUserDTO, User> {
    @Override
    public User convert(UpdateUserDTO source) {
        User user = new User();
        if (source.id() > 0) user.setId(source.id());
        user.setUsername(source.username());
        user.setEmail(source.email());
        user.setRoles(source.roles());
        user.setEnabled(source.enabled());
        user.setTelegramId(source.telegramId());
        user.setTelegramUsername(source.telegramUsername());

        return user;
    }
}
