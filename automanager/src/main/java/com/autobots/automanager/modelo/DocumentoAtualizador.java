package com.autobots.automanager.modelo;

import com.autobots.automanager.entidades.Documento;
import java.util.List;

public class DocumentoAtualizador {
  private StringVerificadorNulo verificador = new StringVerificadorNulo();

  public void atualizar(Documento documento, Documento atualizacao) {
    if (atualizacao.getDataEmissao() != null)
      documento.setDataEmissao(atualizacao.getDataEmissao());
    if (atualizacao != null) {
      if (!verificador.verificar(atualizacao.getTipo())) {
        documento.setTipo(atualizacao.getTipo());
      }
      if (!verificador.verificar(atualizacao.getNumero())) {
        documento.setNumero(atualizacao.getNumero());
      }
    }
  }

  public void atualizar(List<Documento> documentos, List<Documento> atualizacoes) {
    for (Documento atualizacao : atualizacoes) {
      for (Documento documento : documentos) {
        if (atualizacao.getId() != null) {
          if (java.util.Objects.equals(atualizacao.getId(), documento.getId())) {
            atualizar(documento, atualizacao);
          }
        }
      }
    }
  }
}
