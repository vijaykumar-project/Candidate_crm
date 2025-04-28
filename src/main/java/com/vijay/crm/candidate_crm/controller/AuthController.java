package com.vijay.crm.candidate_crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    // Show login page
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";  // Points to login.html in the resources/static folder
    }
}
