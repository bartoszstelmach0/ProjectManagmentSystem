package com.example.projectmanagementsystem.domain.config.exception;

import javax.naming.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {

    public JwtAuthenticationException(String msg) {
        super(msg);
    }

}
