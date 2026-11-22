package com.autobots.automanager.api;

import com.autobots.automanager.entidades.Email;
import com.autobots.automanager.repositorios.EmailRepositorio;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Service;

@Service
public class EmailService extends CadastroService<Email> {
  public EmailService(EmailRepositorio repo, EntityManager em) {
    super(repo, em);
  }
}
