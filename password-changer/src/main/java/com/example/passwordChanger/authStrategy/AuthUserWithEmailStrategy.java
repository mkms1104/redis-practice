package com.example.passwordChanger.authStrategy;

public class AuthUserWithEmailStrategy implements AuthUser {
    @Override
    public boolean isAuth() {
        return false;
    }
}
