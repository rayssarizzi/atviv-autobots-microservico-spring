package com.autobots.automanager.seguranca;

import java.util.Map;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginControle {
  public record Entrada(String nomeUsuario, String senha) {}

  private final Autenticacao autenticacao;

  public LoginControle(Autenticacao autenticacao) {
    this.autenticacao = autenticacao;
  }

  @PostMapping("/api/auth/login")
  public ResponseEntity<Map<String, Object>> entrar(@RequestBody Entrada entrada) {
    return ResponseEntity.ok().cacheControl(CacheControl.noStore())
        .body(autenticacao.entrar(entrada.nomeUsuario(), entrada.senha()));
  }
}
