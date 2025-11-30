package com.lunanotes.mapper;

public record UserDTO(long id,
                      String userName,
                      int numberOfNotes,
                      int numberOfTags) {
}
