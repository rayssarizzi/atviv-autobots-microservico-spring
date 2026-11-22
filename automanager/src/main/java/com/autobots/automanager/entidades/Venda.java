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
public class Venda extends Entidade {
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private LocalDate cadastro = LocalDate.now();

  @NotBlank
  @Column(nullable = false, unique = true)
  private String identificacao;

  @NotNull
  @ManyToOne(optional = false)
  private Empresa empresa;

  @NotNull
  @ManyToOne(optional = false)
  @JsonIncludeProperties("id")
  private Usuario cliente;

  @NotNull
  @ManyToOne(optional = false)
  @JsonIncludeProperties("id")
  private Usuario funcionario;

  @ManyToOne private Veiculo veiculo;

  @javax.validation.Valid
  @ElementCollection(fetch = FetchType.EAGER)
  @OrderColumn(name = "ordem_mercadoria")
  private List<@NotNull ItemMercadoria> mercadorias = new ArrayList<>();

  @javax.validation.Valid
  @ElementCollection(fetch = FetchType.EAGER)
  @OrderColumn(name = "ordem_servico")
  private List<@NotNull ItemServico> servicos = new ArrayList<>();

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Column(precision = 14, scale = 2)
  private BigDecimal total = BigDecimal.ZERO;
}
