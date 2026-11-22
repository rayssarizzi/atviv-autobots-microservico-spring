package com.autobots.automanager.api;

import com.autobots.automanager.entidades.Usuario;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioControle extends CadastroControle<Usuario> {
  public UsuarioControle(UsuarioService service) {
    super(service, "usuarios");
  }
}
