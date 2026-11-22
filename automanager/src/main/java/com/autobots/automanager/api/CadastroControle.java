package com.autobots.automanager.api;

import com.autobots.automanager.entidades.*;
import java.net.URI;
import java.util.*;
import javax.validation.Valid;
import org.springframework.hateoas.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public abstract class CadastroControle<T extends Identificavel> {
  protected final CadastroService<T> service;
  private final String recurso;

  protected CadastroControle(CadastroService<T> service, String recurso) {
    this.service = service;
    this.recurso = recurso;
  }

  protected String url() {
    return ServletUriComponentsBuilder.fromCurrentContextPath()
        .path("/api/" + recurso)
        .toUriString();
  }

  protected EntityModel<T> modelo(T entidade) {
    EntityModel<T> m =
        EntityModel.of(
            entidade,
            Link.of(url() + "/" + entidade.getId()).withSelfRel(),
            Link.of(url()).withRel("colecao"));
    if (entidade instanceof Empresa) {
      for (String rel : List.of("usuarios", "mercadorias", "servicos", "vendas"))
        m.add(Link.of(url() + "/" + entidade.getId() + "/" + rel).withRel(rel));
    }
    if (entidade instanceof Usuario) {
      Usuario u = (Usuario) entidade;
      m.add(
          Link.of(
                  ServletUriComponentsBuilder.fromCurrentContextPath()
                      .path("/api/empresas/" + u.getEmpresa().getId())
                      .toUriString())
              .withRel("empresa"));
      for (String rel : List.of("veiculos", "credenciais", "vendas"))
        m.add(Link.of(url() + "/" + u.getId() + "/" + rel).withRel(rel));
    }
    String raiz = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/").toUriString();
    if (entidade instanceof Veiculo)
      m.add(
          Link.of(raiz + "usuarios/" + ((Veiculo) entidade).getProprietario().getId())
              .withRel("proprietario"));
    if (entidade instanceof Credencial)
      m.add(
          Link.of(raiz + "usuarios/" + ((Credencial) entidade).getUsuario().getId())
              .withRel("usuario"));
    if (entidade instanceof Mercadoria)
      m.add(
          Link.of(raiz + "empresas/" + ((Mercadoria) entidade).getEmpresa().getId())
              .withRel("empresa"));
    if (entidade instanceof Servico)
      m.add(
          Link.of(raiz + "empresas/" + ((Servico) entidade).getEmpresa().getId())
              .withRel("empresa"));
    if (entidade instanceof Venda) {
      Venda v = (Venda) entidade;
      m.add(
          Link.of(raiz + "empresas/" + v.getEmpresa().getId()).withRel("empresa"),
          Link.of(raiz + "usuarios/" + v.getCliente().getId()).withRel("cliente"),
          Link.of(raiz + "usuarios/" + v.getFuncionario().getId()).withRel("funcionario"));
      if (v.getVeiculo() != null)
        m.add(Link.of(raiz + "veiculos/" + v.getVeiculo().getId()).withRel("veiculo"));
    }
    return m;
  }

  @GetMapping
  public CollectionModel<EntityModel<T>> listar() {
    List<EntityModel<T>> itens = new ArrayList<>();
    service.listar().forEach(e -> itens.add(modelo(e)));
    return CollectionModel.of(itens, Link.of(url()).withSelfRel());
  }

  @GetMapping("/{id}")
  public EntityModel<T> obter(@PathVariable Long id) {
    return modelo(service.obter(id));
  }

  @PostMapping
  public ResponseEntity<EntityModel<T>> criar(@Valid @RequestBody T entrada) {
    T salvo = service.criar(entrada);
    return ResponseEntity.created(URI.create(url() + "/" + salvo.getId())).body(modelo(salvo));
  }

  @PutMapping("/{id}")
  public EntityModel<T> atualizar(@PathVariable Long id, @Valid @RequestBody T entrada) {
    return modelo(service.atualizar(id, entrada));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> excluir(@PathVariable Long id) {
    service.excluir(id);
    return ResponseEntity.noContent().build();
  }
}
