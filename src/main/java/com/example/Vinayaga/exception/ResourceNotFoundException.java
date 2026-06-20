package com.example.Vinayaga.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Long id) {
        super("No " + resourceName + " exists with id: " + id);
    }
}
