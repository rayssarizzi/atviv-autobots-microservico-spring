package com.autobots.automanager.api;

import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.repositorios.DocumentoRepositorio;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Service;

@Service
public class DocumentoService extends CadastroService<Documento> {
  public DocumentoService(DocumentoRepositorio repo, EntityManager em) {
    super(repo, em);
  }
}
