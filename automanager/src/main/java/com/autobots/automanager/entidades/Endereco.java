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
public class Endereco extends RepresentationModel<Endereco> implements Identificavel {
  @Id()
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = true)
  @javax.validation.constraints.NotBlank
  private String estado;

  @Column(nullable = false)
  @javax.validation.constraints.NotBlank
  private String cidade;

  @Column(nullable = true)
  @javax.validation.constraints.NotBlank
  private String bairro;

  @Column(nullable = false)
  @javax.validation.constraints.NotBlank
  private String rua;

  @Column(nullable = false)
  @javax.validation.constraints.NotBlank
  private String numero;

  @Column(nullable = true)
  @javax.validation.constraints.NotBlank
  private String codigoPostal;

  @Column(unique = false, nullable = true)
  private String informacoesAdicionais;
}
