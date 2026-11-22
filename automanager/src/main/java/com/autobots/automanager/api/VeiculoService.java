package com.autobots.automanager.api;

import com.autobots.automanager.entidades.*;
import com.autobots.automanager.enumeracoes.*;
import com.autobots.automanager.repositorios.*;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Service;

@Service
public class VeiculoService extends CadastroService<Veiculo> {
  public VeiculoService(VeiculoRepositorio repo, EntityManager em) {
    super(repo, em);
  }

  @Override
  protected void preparar(Veiculo e, Veiculo a) {
    e.setPlaca(e.getPlaca().replaceAll("[^a-zA-Z0-9]", "").toUpperCase(java.util.Locale.ROOT));
    if (!e.getPlaca().matches("[A-Z]{3}[0-9][A-Z0-9][0-9]{2}")) throw erro("Placa inválida");
    e.setProprietario(vinculo(Usuario.class, e.getProprietario()));
    if (!e.getProprietario().getPerfis().contains(PerfilUsuario.CLIENTE))
      throw erro("O proprietário deve ser cliente");
    if (a != null && !a.getProprietario().getId().equals(e.getProprietario().getId()))
      throw erro("A transferência de veículo exige tratamento das vendas anteriores");
  }
}
