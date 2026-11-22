package com.autobots.automanager.api;

import com.autobots.automanager.entidades.Veiculo;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/veiculos")
public class VeiculoControle extends CadastroControle<Veiculo> {
  public VeiculoControle(VeiculoService service) {
    super(service, "veiculos");
  }
}
