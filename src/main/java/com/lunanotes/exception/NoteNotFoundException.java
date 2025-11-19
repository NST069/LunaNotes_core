package com.lunanotes.exception;

public class NoteNotFoundException extends RuntimeException {
    public NoteNotFoundException(String id) {
        super("Could not find note with Id " + id);
    }
}
