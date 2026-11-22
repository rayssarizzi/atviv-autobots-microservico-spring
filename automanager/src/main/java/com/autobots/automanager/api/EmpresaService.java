package com.autobots.automanager.api;

import com.autobots.automanager.entidades.*;
import com.autobots.automanager.enumeracoes.*;
import com.autobots.automanager.repositorios.*;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Service;

@Service
public class EmpresaService extends CadastroService<Empresa> {
  public EmpresaService(EmpresaRepositorio repo, EntityManager em) {
    super(repo, em);
  }

  @Override
  protected void preparar(Empresa e, Empresa a) {
    e.setTelefones(vinculos(Telefone.class, e.getTelefones()));
    if (e.getEndereco() != null) e.setEndereco(vinculo(Endereco.class, e.getEndereco()));
    if (a != null) e.setCadastro(a.getCadastro());
  }
}
