package com.autobots.automanager.api;

import com.autobots.automanager.entidades.Endereco;
import org.springframework.web.bind.annotation.*;

@RestController("apiEnderecoControle")
@RequestMapping("/api/enderecos")
public class EnderecoControle extends CadastroControle<Endereco> {
  public EnderecoControle(EnderecoService service) {
    super(service, "enderecos");
  }
}
