package com.autobots.automanager.api;

import com.autobots.automanager.entidades.Cliente;
import org.springframework.web.bind.annotation.*;

@RestController("apiClienteControle")
@RequestMapping("/api/clientes")
public class ClienteControle extends CadastroControle<Cliente> {
  public ClienteControle(ClienteService service) {
    super(service, "clientes");
  }
}
