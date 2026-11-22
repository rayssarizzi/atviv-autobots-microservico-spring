package com.autobots.automanager.api;

import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.repositorios.EnderecoRepositorio;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Service;

@Service
public class EnderecoService extends CadastroService<Endereco> {
  public EnderecoService(EnderecoRepositorio repo, EntityManager em) {
    super(repo, em);
  }
}
