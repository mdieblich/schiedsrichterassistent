package com.dieblich.handball.schiedsrichterassistent.api;

import com.dieblich.handball.schiedsrichterassistent.mail.EmailServerReadImpl;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiAccessController {
    @Value("${mail.imap.host}")
    private String imapHost;
    @Value("${mail.user}")
    private String botEmailaddress;
    @Value("${mail.password}")
    private String botPassword;

    private ApiAccessRepo repo;

    @SuppressWarnings("unused")
    @PostConstruct
    public void init() {
        EmailServerReadImpl stratoRead = new EmailServerReadImpl(imapHost, 993, botEmailaddress, botPassword);
        repo = new ApiAccessRepoEmail(stratoRead);
    }

    @GetMapping("/access/{email}")
    public String createAccess(@PathVariable String email) throws ApiAccessRepo.ApiAccessRepoException {
        return repo.createAccessKey(email);
    }
}
