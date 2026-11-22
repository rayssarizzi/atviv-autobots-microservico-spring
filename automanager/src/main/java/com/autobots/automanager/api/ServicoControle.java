package com.autobots.automanager.api;

import com.autobots.automanager.entidades.Servico;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/servicos")
public class ServicoControle extends CadastroControle<Servico> {
  public ServicoControle(ServicoService service) {
    super(service, "servicos");
  }
}
