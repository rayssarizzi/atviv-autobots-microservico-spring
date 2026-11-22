package com.autobots.automanager.seguranca;

import com.autobots.automanager.api.CredencialService;
import com.autobots.automanager.entidades.CredencialUsuarioSenha;
import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.enumeracoes.PerfilUsuario;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdministradorInicial implements ApplicationRunner {
  private final EntityManager em;
  private final CredencialService credenciais;
  private final String nome;
  private final String senha;

  public AdministradorInicial(EntityManager em, CredencialService credenciais,
      @Value("${AUTOBOTS_ADMIN_USER:admin}") String nome,
      @Value("${AUTOBOTS_ADMIN_PASSWORD:}") String senha) {
    this.em = em;
    this.credenciais = credenciais;
    this.nome = nome;
    this.senha = senha;
  }

  @Override
  @Transactional
  public void run(ApplicationArguments argumentos) {
    if (senha.isEmpty()) {
      return;
    }
    if (nome.isBlank()) {
      throw new IllegalArgumentException("Informe AUTOBOTS_ADMIN_USER");
    }
    long administradores = em.createQuery(
            "select count(u) from Usuario u where :perfil member of u.perfis", Long.class)
        .setParameter("perfil", PerfilUsuario.ADMINISTRADOR).getSingleResult();
    if (administradores > 0) {
      return;
    }
    Empresa empresa = new Empresa();
    empresa.setRazaoSocial("AutoBots");
    em.persist(empresa);

    Usuario usuario = new Usuario();
    usuario.setNome("Administrador");
    usuario.setEmpresa(empresa);
    usuario.getPerfis().add(PerfilUsuario.ADMINISTRADOR);
    em.persist(usuario);

    CredencialUsuarioSenha credencial = new CredencialUsuarioSenha();
    credencial.setUsuario(usuario);
    credencial.setNomeUsuario(nome);
    credencial.setSenha(senha);
    credenciais.criar(credencial);
  }
}
