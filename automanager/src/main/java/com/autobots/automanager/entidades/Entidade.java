package com.autobots.automanager.entidades;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class Entidade implements Identificavel {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Version
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long versao;
}
