package com.autobots.automanager.api;

import com.autobots.automanager.entidades.*;
import com.autobots.automanager.enumeracoes.*;
import com.autobots.automanager.repositorios.*;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Service;

@Service
public class MercadoriaService extends CadastroService<Mercadoria> {
  public MercadoriaService(MercadoriaRepositorio repo, EntityManager em) {
    super(repo, em);
  }

  @Override
  protected void preparar(Mercadoria e, Mercadoria a) {
    e.setEmpresa(vinculo(Empresa.class, e.getEmpresa()));
    if (a != null) {
      mesmaEmpresa(a.getEmpresa(), e.getEmpresa());
      e.setCadastro(a.getCadastro());
    }
    if (e.getFabricacao().isAfter(e.getValidade())) throw erro("Fabricação posterior à validade");
    if (e.getFornecedor() != null) {
      e.setFornecedor(vinculo(Usuario.class, e.getFornecedor()));
      mesmaEmpresa(e.getEmpresa(), e.getFornecedor().getEmpresa());
      if (!e.getFornecedor().getPerfis().contains(PerfilUsuario.FORNECEDOR))
        throw erro("Usuário deve ser fornecedor");
    }
  }
}
