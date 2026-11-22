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
public class Telefone extends RepresentationModel<Telefone> implements Identificavel {
  @Id()
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  @javax.validation.constraints.NotBlank
  @javax.validation.constraints.Pattern(regexp = "[0-9]{2}")
  private String ddd;

  @Column
  @javax.validation.constraints.NotBlank
  @javax.validation.constraints.Pattern(regexp = "[0-9]{8,9}")
  private String numero;
}
