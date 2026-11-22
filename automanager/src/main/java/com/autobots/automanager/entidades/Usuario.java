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
public class Usuario extends Entidade {
  @NotBlank
  @Column(nullable = false)
  private String nome;

  private String nomeSocial;

  @NotNull
  @ManyToOne(optional = false)
  private Empresa empresa;

  @NotEmpty
  @ElementCollection(fetch = FetchType.EAGER)
  @Enumerated(EnumType.STRING)
  private Set<PerfilUsuario> perfis = new LinkedHashSet<>();

  @ManyToMany(fetch = FetchType.EAGER)
  private Set<Telefone> telefones = new LinkedHashSet<>();

  @OneToOne private Endereco endereco;

  @ManyToMany(fetch = FetchType.EAGER)
  private Set<Documento> documentos = new LinkedHashSet<>();

  @ManyToMany(fetch = FetchType.EAGER)
  private Set<Email> emails = new LinkedHashSet<>();

  @JsonIgnore
  @OneToMany(mappedBy = "usuario")
  private Set<Credencial> credenciais = new LinkedHashSet<>();

  @JsonIgnore
  @OneToMany(mappedBy = "proprietario")
  private Set<Veiculo> veiculos = new LinkedHashSet<>();

  @JsonIgnore
  @OneToMany(mappedBy = "fornecedor")
  private Set<Mercadoria> mercadorias = new LinkedHashSet<>();
}
