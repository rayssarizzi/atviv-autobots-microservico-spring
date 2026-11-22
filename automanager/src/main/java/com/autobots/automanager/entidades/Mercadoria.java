package com.autobots.automanager.entidades;

import com.autobots.automanager.enumeracoes.*;
import com.fasterxml.jackson.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Mercadoria extends Entidade {
  @NotNull private LocalDate validade;
  @NotNull private LocalDate fabricacao;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private LocalDate cadastro = LocalDate.now();

  @NotBlank
  @Column(nullable = false)
  private String nome;

  @Min(0)
  @Column(nullable = false)
  private long quantidade;

  @NotNull
  @DecimalMin("0.00")
  @Digits(integer = 10, fraction = 2)
  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal valor;

  private String descricao;

  @NotNull
  @ManyToOne(optional = false)
  private Empresa empresa;

  @JsonIncludeProperties("id")
  @ManyToOne private Usuario fornecedor;
}
