package com.autobots.automanager.api;

import com.autobots.automanager.entidades.Telefone;
import org.springframework.web.bind.annotation.*;

@RestController("apiTelefoneControle")
@RequestMapping("/api/telefones")
public class TelefoneControle extends CadastroControle<Telefone> {
  public TelefoneControle(TelefoneService service) {
    super(service, "telefones");
  }
}
