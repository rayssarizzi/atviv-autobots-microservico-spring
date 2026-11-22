package com.autobots.automanager.api;

import com.autobots.automanager.entidades.Empresa;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaControle extends CadastroControle<Empresa> {
  public EmpresaControle(EmpresaService service) {
    super(service, "empresas");
  }
}
