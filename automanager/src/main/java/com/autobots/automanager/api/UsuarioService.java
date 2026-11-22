package com.autobots.automanager.api;

import com.autobots.automanager.entidades.*;
import com.autobots.automanager.enumeracoes.*;
import com.autobots.automanager.repositorios.*;
import com.autobots.automanager.seguranca.Acesso;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService extends CadastroService<Usuario> {
  private final Acesso acesso;

  public UsuarioService(UsuarioRepositorio repo, EntityManager em, Acesso acesso) {
    super(repo, em);
    this.acesso = acesso;
  }

  @Override
  @Transactional(readOnly = true)
  public List<Usuario> listar() {
    return super.listar().stream().filter(acesso::podeConsultar).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public Usuario obter(Long id) {
    Usuario usuario = super.obter(id);
    if (!acesso.podeConsultar(usuario)) {
      throw acesso.proibido();
    }
    return usuario;
  }

  @Override
  @Transactional
  public void excluir(Long id) {
    acesso.exigirGestao(obter(id));
    super.excluir(id);
  }

  @Override
  protected void preparar(Usuario e, Usuario a) {
    acesso.exigirGestao(e);
    if (a != null) acesso.exigirGestao(a);
    e.setEmpresa(vinculo(Empresa.class, e.getEmpresa()));
    if (e.getPerfis().contains(null)) throw erro("Perfil inválido");
    if (a != null && !a.getEmpresa().getId().equals(e.getEmpresa().getId()))
      throw erro("A empresa do usuário não pode ser alterada");
    if (a != null) {
      if (!e.getPerfis().contains(PerfilUsuario.CLIENTE)
          && (em.createQuery(
                          "select count(v) from Veiculo v where v.proprietario.id=:id", Long.class)
                      .setParameter("id", a.getId())
                      .getSingleResult()
                  > 0
              || em.createQuery("select count(v) from Venda v where v.cliente.id=:id", Long.class)
                      .setParameter("id", a.getId())
                      .getSingleResult()
                  > 0)) throw erro("Perfil cliente em uso");
      if (!e.getPerfis().contains(PerfilUsuario.FUNCIONARIO)
          && !e.getPerfis().contains(PerfilUsuario.VENDEDOR)
          && em.createQuery("select count(v) from Venda v where v.funcionario.id=:id", Long.class)
                  .setParameter("id", a.getId())
                  .getSingleResult()
              > 0) throw erro("Perfil funcionário em uso");
      if (!e.getPerfis().contains(PerfilUsuario.FORNECEDOR)
          && em.createQuery(
                      "select count(m) from Mercadoria m where m.fornecedor.id=:id", Long.class)
                  .setParameter("id", a.getId())
                  .getSingleResult()
              > 0) throw erro("Perfil fornecedor em uso");
    }
    e.setTelefones(vinculos(Telefone.class, e.getTelefones()));
    e.setDocumentos(vinculos(Documento.class, e.getDocumentos()));
    e.setEmails(vinculos(Email.class, e.getEmails()));
    if (e.getEndereco() != null) e.setEndereco(vinculo(Endereco.class, e.getEndereco()));
  }
}
