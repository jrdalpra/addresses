package com.zeroturnaround.rebellabs.addresses.api.exceptions;

public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NotFoundException() {
        this("Resource not found");
    }

    public NotFoundException(String message) {
        super(message);
    }

}
