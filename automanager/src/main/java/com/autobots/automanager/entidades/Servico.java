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
@Entity
public class Servico extends Entidade {
  @NotBlank
  @Column(nullable = false)
  private String nome;

  @NotNull
  @DecimalMin("0.00")
  @Digits(integer = 10, fraction = 2)
  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal valor;

  private String descricao;

  @NotNull
  @ManyToOne(optional = false)
  private Empresa empresa;
}
