package com.example.Vinayaga.exception;

public class DuplicateProjectCodeException extends RuntimeException {

    public DuplicateProjectCodeException(String projectCode) {
        super("A project with code '" + projectCode + "' already exists");
    }
}
