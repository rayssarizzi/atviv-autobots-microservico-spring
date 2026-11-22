package com.autobots.automanager.api;

import com.autobots.automanager.entidades.Credencial;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/credenciais")
public class CredencialControle extends CadastroControle<Credencial> {
  public CredencialControle(CredencialService service) {
    super(service, "credenciais");
  }
}
