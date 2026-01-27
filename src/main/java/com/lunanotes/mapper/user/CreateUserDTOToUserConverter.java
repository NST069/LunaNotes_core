package com.lunanotes.mapper.user;

import com.lunanotes.model.User;
import com.lunanotes.util.UserRole;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CreateUserDTOToUserConverter implements Converter<CreateUserDTO, User> {
    @Override
    public User convert(CreateUserDTO source) {
        User user = new User();
        user.setUsername(source.username());
        user.setPassword(source.password());
        user.setRoles(UserRole.USER.name());
        user.setEmail(source.email());
        user.setTelegramId(source.telegramId());
        user.setTelegramUsername(source.telegramUsername());

        return user;
    }
}
