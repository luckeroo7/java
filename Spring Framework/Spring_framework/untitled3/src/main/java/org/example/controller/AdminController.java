package org.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    @GetMapping("/admin")
    public String adminPanel() {
        return "Добро пожаловать в админку!";
    }

    @GetMapping("/public/info")
    public String publicInfo() {
        return "Это общедоступная информация.";
    }
}