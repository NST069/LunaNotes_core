package com.lunanotes.mapper;

import com.lunanotes.model.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserDTOToUserConverter implements Converter<UserDTO, User> {
    @Override
    public User convert(UserDTO source) {
        User user = new User();
        if (source.id() > 0) user.setId(source.id());
        user.setUsername(source.username());
        user.setRoles(source.roles());

        return user;
    }
}
