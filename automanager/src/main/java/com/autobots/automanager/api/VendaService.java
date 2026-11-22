package com.autobots.automanager.api;

import com.autobots.automanager.entidades.*;
import com.autobots.automanager.enumeracoes.*;
import com.autobots.automanager.repositorios.*;
import com.autobots.automanager.seguranca.Acesso;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VendaService extends CadastroService<Venda> {
  private final Acesso acesso;

  public VendaService(VendaRepositorio repo, EntityManager em, Acesso acesso) {
    super(repo, em);
    this.acesso = acesso;
  }

  @Override
  @Transactional(readOnly = true)
  public List<Venda> listar() {
    return super.listar().stream().filter(acesso::podeConsultar).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public Venda obter(Long id) {
    Venda venda = super.obter(id);
    if (!acesso.podeConsultar(venda)) {
      throw acesso.proibido();
    }
    return venda;
  }

  @Override
  protected void preparar(Venda e, Venda a) {
    e.setEmpresa(vinculo(Empresa.class, e.getEmpresa()));
    e.setCliente(vinculo(Usuario.class, e.getCliente()));
    if (e.getFuncionario() != null) acesso.exigirAutoria(e);
    e.setFuncionario(vinculo(Usuario.class, e.getFuncionario()));
    mesmaEmpresa(e.getEmpresa(), e.getCliente().getEmpresa());
    mesmaEmpresa(e.getEmpresa(), e.getFuncionario().getEmpresa());
    if (!e.getCliente().getPerfis().contains(PerfilUsuario.CLIENTE)
        || (!e.getFuncionario().getPerfis().contains(PerfilUsuario.FUNCIONARIO)
            && !e.getFuncionario().getPerfis().contains(PerfilUsuario.VENDEDOR)))
      throw erro("Perfis de cliente/funcionário inválidos");
    if (e.getVeiculo() != null) {
      e.setVeiculo(vinculo(Veiculo.class, e.getVeiculo()));
      if (!e.getVeiculo().getProprietario().getId().equals(e.getCliente().getId()))
        throw erro("Veículo não pertence ao cliente");
    }
    if (e.getMercadorias() == null
        || e.getServicos() == null
        || (e.getMercadorias().isEmpty() && e.getServicos().isEmpty()))
      throw erro("Venda precisa de mercadoria ou serviço");
    if (!e.getServicos().isEmpty() && e.getVeiculo() == null)
      throw erro("Serviço prestado requer veículo");
    if (a != null) {
      mesmaEmpresa(a.getEmpresa(), e.getEmpresa());
      e.setCadastro(a.getCadastro());
      devolverEstoque(a);
    }
    BigDecimal total = BigDecimal.ZERO;
    Set<Long> vistos = new HashSet<>();
    for (ItemMercadoria item : e.getMercadorias()) {
      Mercadoria m = vinculo(Mercadoria.class, item.getMercadoria());
      mesmaEmpresa(e.getEmpresa(), m.getEmpresa());
      if (!vistos.add(m.getId())) throw erro("Mercadoria repetida: utilize quantidade");
      if (m.getValidade().isBefore(LocalDate.now())) throw erro("Mercadoria vencida");
      if (m.getQuantidade() < item.getQuantidade()) throw erro("Estoque insuficiente");
      item.setMercadoria(m);
      BigDecimal preco =
          a == null
              ? m.getValor()
              : a.getMercadorias().stream()
                  .filter(i -> i.getMercadoria().getId().equals(m.getId()))
                  .map(ItemMercadoria::getPrecoUnitario)
                  .findFirst()
                  .orElse(m.getValor());
      item.setPrecoUnitario(preco);
      m.setQuantidade(m.getQuantidade() - item.getQuantidade());
      total = total.add(preco.multiply(BigDecimal.valueOf(item.getQuantidade())));
    }
    vistos.clear();
    for (ItemServico item : e.getServicos()) {
      Servico s = vinculo(Servico.class, item.getServico());
      mesmaEmpresa(e.getEmpresa(), s.getEmpresa());
      if (!vistos.add(s.getId())) throw erro("Serviço repetido: utilize quantidade");
      item.setServico(s);
      BigDecimal preco =
          a == null
              ? s.getValor()
              : a.getServicos().stream()
                  .filter(i -> i.getServico().getId().equals(s.getId()))
                  .map(ItemServico::getPrecoUnitario)
                  .findFirst()
                  .orElse(s.getValor());
      item.setPrecoUnitario(preco);
      total = total.add(preco.multiply(BigDecimal.valueOf(item.getQuantidade())));
    }
    e.setTotal(total);
  }

  private void devolverEstoque(Venda venda) {
    for (ItemMercadoria i : venda.getMercadorias())
      i.getMercadoria()
          .setQuantidade(Math.addExact(i.getMercadoria().getQuantidade(), i.getQuantidade()));
  }

  @Override
  @Transactional
  public void excluir(Long id) {
    Venda venda = obter(id);
    devolverEstoque(venda);
    repo.delete(venda);
    repo.flush();
  }
}
