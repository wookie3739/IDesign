package com.my.interrior.client.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    @GetMapping("login")
    public String LoginPage(){
        return "client/login";
    }
}