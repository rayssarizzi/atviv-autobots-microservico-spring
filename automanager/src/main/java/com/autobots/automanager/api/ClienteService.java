package com.autobots.automanager.api;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Service;

@Service
public class ClienteService extends CadastroService<Cliente> {
  public ClienteService(ClienteRepositorio repo, EntityManager em) {
    super(repo, em);
  }

  @Override
  protected void copiar(Cliente entrada, Cliente anterior) {
    org.springframework.beans.BeanUtils.copyProperties(
        entrada, anterior, "id", "documentos", "telefones");
    anterior.getDocumentos().clear();
    anterior.getDocumentos().addAll(entrada.getDocumentos());
    anterior.getTelefones().clear();
    anterior.getTelefones().addAll(entrada.getTelefones());
  }
}
