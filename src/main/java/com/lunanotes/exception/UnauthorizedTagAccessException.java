package com.lunanotes.exception;

public class UnauthorizedTagAccessException extends RuntimeException {
    public UnauthorizedTagAccessException(String tagId, String noteId) {
        super("Tag("+tagId+") does not belong to note's("+noteId+") author");
    }
}
