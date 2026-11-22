package com.autobots.automanager.api;

import com.autobots.automanager.entidades.Mercadoria;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mercadorias")
public class MercadoriaControle extends CadastroControle<Mercadoria> {
  public MercadoriaControle(MercadoriaService service) {
    super(service, "mercadorias");
  }
}
