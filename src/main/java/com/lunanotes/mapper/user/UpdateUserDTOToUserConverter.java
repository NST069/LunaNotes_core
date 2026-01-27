package com.lunanotes.mapper.user;

import com.lunanotes.model.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UpdateUserDTOToUserConverter implements Converter<UpdateUserDTO, User> {
    @Override
    public User convert(UpdateUserDTO source) {
        User user = new User();
        user.setUsername(source.username());
        if(source.password() != null && !source.password().isBlank()) user.setPassword(source.password());
        user.setEmail(source.email());
        user.setRoles(source.roles());
        user.setEnabled(source.enabled());
        user.setTelegramId(source.telegramId());
        user.setTelegramUsername(source.telegramUsername());

        return user;
    }
}
