package com.example.passwordChanger.authStrategy;

import org.springframework.stereotype.Component;

@Component
public class AuthUserWithPhoneStrategy implements AuthUser {
    @Override
    public boolean isAuth() {
        return true;
    }
}
