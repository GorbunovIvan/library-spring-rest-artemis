package com.example.exceptions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Class<?> entityType, String field, String value) {
        super(String.format("%s not found by %s = '%s'", entityType.getSimpleName(), field, value));
    }
}
