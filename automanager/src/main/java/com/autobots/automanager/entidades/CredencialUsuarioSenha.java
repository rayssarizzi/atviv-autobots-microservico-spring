package com.autobots.automanager.entidades;

import com.autobots.automanager.enumeracoes.*;
import com.fasterxml.jackson.annotation.*;
import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class CredencialUsuarioSenha extends Credencial {
  @NotBlank
  @Column(nullable = false, unique = true)
  private String nomeUsuario;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Column(nullable = false)
  private String senha;
}
