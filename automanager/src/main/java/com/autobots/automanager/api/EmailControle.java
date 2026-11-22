package com.autobots.automanager.api;

import com.autobots.automanager.entidades.Email;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emails")
public class EmailControle extends CadastroControle<Email> {
  public EmailControle(EmailService service) {
    super(service, "emails");
  }
}
