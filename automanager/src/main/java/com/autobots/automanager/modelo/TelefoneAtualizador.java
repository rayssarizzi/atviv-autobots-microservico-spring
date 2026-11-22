package com.autobots.automanager.modelo;

import com.autobots.automanager.entidades.Telefone;
import java.util.List;

public class TelefoneAtualizador {
  private StringVerificadorNulo verificador = new StringVerificadorNulo();

  public void atualizar(Telefone telefone, Telefone atualizacao) {
    if (atualizacao != null) {
      if (!verificador.verificar(atualizacao.getDdd())) {
        telefone.setDdd(atualizacao.getDdd());
      }
      if (!verificador.verificar(atualizacao.getNumero())) {
        telefone.setNumero(atualizacao.getNumero());
      }
    }
  }

  public void atualizar(List<Telefone> telefones, List<Telefone> atualizacoes) {
    for (Telefone atualizacao : atualizacoes) {
      for (Telefone telefone : telefones) {
        if (atualizacao.getId() != null) {
          if (java.util.Objects.equals(atualizacao.getId(), telefone.getId())) {
            atualizar(telefone, atualizacao);
          }
        }
      }
    }
  }
}
