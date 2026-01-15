package com.lunanotes.mapper;

import jakarta.validation.constraints.NotEmpty;

public record UserDTO(long id,
                      @NotEmpty(message = "username is required.")
                      String userName,
                      @NotEmpty(message = "roles are required.")
                      String roles) {
}
