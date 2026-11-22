package com.autobots.automanager.api;

import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.repositorios.TelefoneRepositorio;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Service;

@Service
public class TelefoneService extends CadastroService<Telefone> {
  public TelefoneService(TelefoneRepositorio repo, EntityManager em) {
    super(repo, em);
  }
}
