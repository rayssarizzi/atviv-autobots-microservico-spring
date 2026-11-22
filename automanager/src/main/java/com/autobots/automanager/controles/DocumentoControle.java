package com.autobots.automanager.controles;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.modelo.DocumentoAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.DocumentoRepositorio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@org.springframework.transaction.annotation.Transactional
@RequestMapping("/documento")
public class DocumentoControle {
  @Autowired private DocumentoRepositorio repositorio;
  @Autowired private ClienteRepositorio clienteRepositorio;

  @GetMapping("/documento/{id}")
  public Documento obterDocumento(@PathVariable long id) {
    return repositorio
        .findById(id)
        .orElseThrow(
            () ->
                new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND, "Registro não encontrado"));
  }

  @GetMapping("/documentos")
  public List<Documento> obterDocumentos() {
    return repositorio.findAll();
  }

  @GetMapping("/cliente/{clienteId}")
  public List<Documento> obterDocumentosDoCliente(@PathVariable long clienteId) {
    Cliente cliente =
        clienteRepositorio
            .findById(clienteId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    return cliente.getDocumentos();
  }

  @PostMapping("/cadastro/{clienteId}")
  public void cadastrarDocumento(@PathVariable long clienteId, @RequestBody Documento documento) {
    Cliente cliente =
        clienteRepositorio
            .findById(clienteId)
            .orElseThrow(
                () ->
                    new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Registro não encontrado"));
    cliente.getDocumentos().add(documento);
    clienteRepositorio.save(cliente);
  }

  @PutMapping("/atualizar")
  public void atualizarDocumento(@RequestBody Documento atualizacao) {
    Documento documento = repositorio.getById(atualizacao.getId());
    DocumentoAtualizador atualizador = new DocumentoAtualizador();
    atualizador.atualizar(documento, atualizacao);
    repositorio.save(documento);
  }

  @DeleteMapping("/excluir/{clienteId}")
  public void excluirDocumento(@PathVariable long clienteId, @RequestBody Documento exclusao) {
    Cliente cliente =
        clienteRepositorio
            .findById(clienteId)
            .orElseThrow(
                () ->
                    new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Registro não encontrado"));
    cliente.getDocumentos().removeIf(d -> java.util.Objects.equals(d.getId(), exclusao.getId()));
    clienteRepositorio.save(cliente);
  }
}
