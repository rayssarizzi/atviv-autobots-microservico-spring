package com.autobots.automanager.entidades;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
public class Documento extends RepresentationModel<Documento> implements Identificavel {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  @javax.validation.constraints.NotBlank
  @javax.validation.constraints.Pattern(regexp = "CPF|CNPJ|RG|CNH|PASSAPORTE")
  private String tipo;

  @Column(unique = true)
  @javax.validation.constraints.NotBlank
  private String numero;

  @javax.validation.constraints.NotNull
  @Column(nullable = false)
  private java.time.LocalDate dataEmissao;
}
