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
public class Veiculo extends Entidade {
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TipoVeiculo tipo;

  @NotBlank
  @Column(nullable = false)
  private String modelo;

  @NotBlank
  @Column(nullable = false, unique = true)
  private String placa;

  @NotNull
  @ManyToOne(optional = false)
  @JsonIncludeProperties("id")
  private Usuario proprietario;

  @JsonIgnore
  @OneToMany(mappedBy = "veiculo")
  private Set<Venda> vendas = new LinkedHashSet<>();
}
