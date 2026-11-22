package com.autobots.automanager.api;

import com.autobots.automanager.entidades.*;
import com.autobots.automanager.enumeracoes.*;
import com.autobots.automanager.repositorios.*;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Service;

@Service
public class CredencialService extends CadastroService<Credencial> {
  public CredencialService(CredencialRepositorio repo, EntityManager em) {
    super(repo, em);
  }

  @Override
  protected void preparar(Credencial e, Credencial a) {
    e.setUsuario(vinculo(Usuario.class, e.getUsuario()));
    if (a != null) {
      if (!a.getClass().equals(e.getClass()))
        throw erro("Não é possível trocar o tipo da credencial");
      if (!a.getUsuario().getId().equals(e.getUsuario().getId()))
        throw erro("Não é possível trocar o titular");
      e.setCriacao(a.getCriacao());
      e.setUltimoAcesso(a.getUltimoAcesso());
    }
    if (e instanceof CredencialUsuarioSenha) {
      CredencialUsuarioSenha c = (CredencialUsuarioSenha) e;
      if (c.getSenha() == null && a instanceof CredencialUsuarioSenha)
        c.setSenha(((CredencialUsuarioSenha) a).getSenha());
      else {
        if (c.getSenha() == null
            || c.getSenha().isBlank()
            || c.getSenha().length() < 8
            || c.getSenha().getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72)
          throw erro("Senha deve ter pelo menos 8 caracteres e no máximo 72 bytes");
        c.setSenha(
            new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
                .encode(c.getSenha()));
      }
    }
  }
}
