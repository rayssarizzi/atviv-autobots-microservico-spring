package com.autobots.automanager.entidades;

import com.autobots.automanager.enumeracoes.*;
import com.fasterxml.jackson.annotation.*;
import java.math.BigDecimal;
import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class ItemServico {
  @NotNull
  @ManyToOne(optional = false)
  private Servico servico;

  @Min(1)
  private int quantidade;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Column(precision = 12, scale = 2)
  private BigDecimal precoUnitario;
}
