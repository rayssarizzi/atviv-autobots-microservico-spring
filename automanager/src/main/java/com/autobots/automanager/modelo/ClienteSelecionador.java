package com.autobots.automanager.modelo;

import com.autobots.automanager.entidades.Cliente;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ClienteSelecionador {
  public Cliente selecionar(List<Cliente> clientes, long id) {
    Cliente selecionado = null;
    for (Cliente cliente : clientes) {
      if (cliente.getId() == id) {
        selecionado = cliente;
      }
    }
    return selecionado;
  }
}
