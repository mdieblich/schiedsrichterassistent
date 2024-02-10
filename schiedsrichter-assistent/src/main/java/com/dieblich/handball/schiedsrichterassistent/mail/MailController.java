package com.dieblich.handball.schiedsrichterassistent.mail;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MailController {

    @GetMapping("/mailtest")
    public String mailtest() {

        return "Ich bin da!";
    }

}
