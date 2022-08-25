package com.example.passwordChanger.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PasswordChangeController {

    @GetMapping("auth")
    public ModelAndView auth() {
        return new ModelAndView();
    }

    @GetMapping("change")
    public ModelAndView change() {
        return new ModelAndView();
    }
}
