package com.autobots.automanager.api;

import com.autobots.automanager.entidades.*;
import com.autobots.automanager.enumeracoes.*;
import com.autobots.automanager.repositorios.*;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Service;

@Service
public class ServicoService extends CadastroService<Servico> {
  public ServicoService(ServicoRepositorio repo, EntityManager em) {
    super(repo, em);
  }

  @Override
  protected void preparar(Servico e, Servico a) {
    e.setEmpresa(vinculo(Empresa.class, e.getEmpresa()));
    if (a != null) mesmaEmpresa(a.getEmpresa(), e.getEmpresa());
  }
}
