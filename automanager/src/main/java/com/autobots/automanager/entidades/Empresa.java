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
public class Empresa extends Entidade {
  @NotBlank
  @Column(nullable = false)
  private String razaoSocial;

  private String nomeFantasia;

  @Column(nullable = false)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private LocalDate cadastro = LocalDate.now();

  @OneToOne private Endereco endereco;

  @ManyToMany(fetch = FetchType.EAGER)
  private Set<Telefone> telefones = new LinkedHashSet<>();

  @JsonIgnore
  @OneToMany(mappedBy = "empresa")
  private Set<Usuario> usuarios = new LinkedHashSet<>();

  @JsonIgnore
  @OneToMany(mappedBy = "empresa")
  private Set<Mercadoria> mercadorias = new LinkedHashSet<>();

  @JsonIgnore
  @OneToMany(mappedBy = "empresa")
  private Set<Servico> servicos = new LinkedHashSet<>();

  @JsonIgnore
  @OneToMany(mappedBy = "empresa")
  private Set<Venda> vendas = new LinkedHashSet<>();
}
