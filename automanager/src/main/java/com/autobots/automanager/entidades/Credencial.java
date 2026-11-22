package com.autobots.automanager.entidades;

import com.autobots.automanager.enumeracoes.*;
import com.fasterxml.jackson.annotation.*;
import java.time.LocalDate;
import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "tipo")
@JsonSubTypes({
  @JsonSubTypes.Type(value = CredencialUsuarioSenha.class, name = "USUARIO_SENHA"),
  @JsonSubTypes.Type(value = CredencialCodigoBarra.class, name = "CODIGO_BARRA")
})
public abstract class Credencial extends Entidade {
  @NotNull
  @ManyToOne(optional = false)
  private Usuario usuario;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private LocalDate criacao = LocalDate.now();

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private LocalDate ultimoAcesso;

  private boolean inativo;
}
