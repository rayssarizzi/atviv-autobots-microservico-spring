package com.autobots.automanager.api;

import com.autobots.automanager.entidades.*;
import java.util.*;
import javax.persistence.EntityManager;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

public class CadastroService<T extends Identificavel> {
  protected final JpaRepository<T, Long> repo;
  protected final EntityManager em;

  public CadastroService(JpaRepository<T, Long> repo, EntityManager em) {
    this.repo = repo;
    this.em = em;
  }

  @Transactional(readOnly = true)
  public List<T> listar() {
    return repo.findAll();
  }

  @Transactional(readOnly = true)
  public T obter(Long id) {
    return repo.findById(id)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro não encontrado"));
  }

  @Transactional
  public T criar(T entrada) {
    if (entrada.getId() != null) throw erro("Não envie id no cadastro");
    preparar(entrada, null);
    return repo.saveAndFlush(entrada);
  }

  @Transactional
  public T atualizar(Long id, T entrada) {
    T anterior = obter(id);
    if (entrada.getId() != null && !id.equals(entrada.getId()))
      throw erro("Id do corpo difere da URL");
    preparar(entrada, anterior);
    copiar(entrada, anterior);
    return repo.saveAndFlush(anterior);
  }

  @Transactional
  public void excluir(Long id) {
    repo.delete(obter(id));
    repo.flush();
  }

  protected void preparar(T entrada, T anterior) {}

  protected void copiar(T entrada, T anterior) {
    BeanUtils.copyProperties(entrada, anterior, "id", "versao");
  }

  protected <E extends Identificavel> E vinculo(Class<E> tipo, E referencia) {
    if (referencia == null || referencia.getId() == null)
      throw erro("Informe o id de " + tipo.getSimpleName());
    E persistido = em.find(tipo, referencia.getId());
    if (persistido == null)
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, tipo.getSimpleName() + " não encontrado");
    return persistido;
  }

  protected <E extends Identificavel> Set<E> vinculos(Class<E> tipo, Set<E> referencias) {
    if (referencias == null) throw erro("Coleção não pode ser nula");
    Set<E> saida = new LinkedHashSet<>();
    referencias.forEach(r -> saida.add(vinculo(tipo, r)));
    return saida;
  }

  protected ResponseStatusException erro(String msg) {
    return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
  }

  protected void mesmaEmpresa(Empresa a, Empresa b) {
    if (!a.getId().equals(b.getId())) throw erro("Os registros devem pertencer à mesma empresa");
  }
}
