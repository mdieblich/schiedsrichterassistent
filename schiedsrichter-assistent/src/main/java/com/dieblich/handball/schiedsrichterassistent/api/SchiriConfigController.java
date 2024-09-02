package com.dieblich.handball.schiedsrichterassistent.api;

import com.dieblich.handball.schiedsrichterassistent.config.SchiriConfiguration;
import com.dieblich.handball.schiedsrichterassistent.config.SchiriRepo;
import com.dieblich.handball.schiedsrichterassistent.mail.EmailServerReadImpl;
import com.dieblich.handball.schiedsrichterassistent.mail.SchiriRepoEmail;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
public class SchiriConfigController {

    @Value("${mail.imap.host}")
    private String imapHost;
    @Value("${mail.smtp.host}")
    private String smtpHost;
    @Value("${mail.user}")
    private String botEmailaddress;
    @Value("${mail.password}")
    private String botPassword;

    private SchiriRepo schiriRepo;

    @PostConstruct
    public void init() {
        EmailServerReadImpl stratoRead = new EmailServerReadImpl(imapHost, 993, botEmailaddress, botPassword);
        schiriRepo = new SchiriRepoEmail(stratoRead);
    }

    @GetMapping("/config/{email}")
    public SchiriConfiguration getConfig(@PathVariable String email){
        try {
            Optional<SchiriConfiguration> optionalConfig = schiriRepo.findConfigByEmail(email);
            if(optionalConfig.isEmpty()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            return optionalConfig.get();
        } catch (SchiriRepo.SchiriRepoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
