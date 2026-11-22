package com.autobots.automanager.api;

import com.autobots.automanager.entidades.Venda;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vendas")
public class VendaControle extends CadastroControle<Venda> {
  public VendaControle(VendaService service) {
    super(service, "vendas");
  }
}
