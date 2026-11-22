package com.autobots.automanager.api;

import com.autobots.automanager.entidades.*;
import java.util.*;
import javax.persistence.EntityManager;
import org.springframework.hateoas.*;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api")
@Transactional(readOnly = true)
public class VinculosControle {
  private final EntityManager em;

  public VinculosControle(EntityManager em) {
    this.em = em;
  }

  @GetMapping
  public RepresentationModel<?> inicio() {
    RepresentationModel<?> m = new RepresentationModel<>();
    for (String r :
        List.of(
            "clientes",
            "empresas",
            "usuarios",
            "veiculos",
            "mercadorias",
            "servicos",
            "vendas",
            "credenciais",
            "emails",
            "documentos",
            "enderecos",
            "telefones")) m.add(Link.of(base() + "/" + r).withRel(r));
    return m;
  }

  private String base() {
    return ServletUriComponentsBuilder.fromCurrentContextPath().path("/api").toUriString();
  }

  @GetMapping("/empresas/{id}/{recurso:usuarios|mercadorias|servicos|vendas}")
  public CollectionModel<EntityModel<Identificavel>> empresa(
      @PathVariable Long id, @PathVariable String recurso) {
    exigir(Empresa.class, id);
    Map<String, Class<? extends Identificavel>> tipos =
        Map.of(
            "usuarios",
            Usuario.class,
            "mercadorias",
            Mercadoria.class,
            "servicos",
            Servico.class,
            "vendas",
            Venda.class);
    return consultar(
        tipos.get(recurso), "e.empresa.id = :id", id, recurso, "empresas/" + id + "/" + recurso);
  }

  @GetMapping("/usuarios/{id}/{recurso:veiculos|credenciais|vendas}")
  public CollectionModel<EntityModel<Identificavel>> usuario(
      @PathVariable Long id, @PathVariable String recurso) {
    exigir(Usuario.class, id);
    Class<? extends Identificavel> tipo =
        recurso.equals("veiculos")
            ? Veiculo.class
            : recurso.equals("credenciais") ? Credencial.class : Venda.class;
    String filtro =
        recurso.equals("veiculos")
            ? "e.proprietario.id = :id"
            : recurso.equals("credenciais")
                ? "e.usuario.id = :id"
                : "(e.cliente.id = :id or e.funcionario.id = :id)";
    return consultar(tipo, filtro, id, recurso, "usuarios/" + id + "/" + recurso);
  }

  private void exigir(Class<?> tipo, Long id) {
    if (em.find(tipo, id) == null)
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro não encontrado");
  }

  private CollectionModel<EntityModel<Identificavel>> consultar(
      Class<? extends Identificavel> tipo, String filtro, Long id, String recurso, String caminho) {
    List<? extends Identificavel> itens =
        em.createQuery("select e from " + tipo.getSimpleName() + " e where " + filtro, tipo)
            .setParameter("id", id)
            .getResultList();
    List<EntityModel<Identificavel>> modelos = new ArrayList<>();
    for (Identificavel e : itens)
      modelos.add(
          EntityModel.of(e, Link.of(base() + "/" + recurso + "/" + e.getId()).withSelfRel()));
    return CollectionModel.of(modelos, Link.of(base() + "/" + caminho).withSelfRel());
  }
}
