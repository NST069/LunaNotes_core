package com.lunanotes.exception;

public class TagNotFoundException extends RuntimeException {
    public TagNotFoundException(String id) {
        super("Could not find tag with Id " + id);
    }
}
