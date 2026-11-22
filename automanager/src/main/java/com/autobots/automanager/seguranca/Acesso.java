package com.autobots.automanager.seguranca;

import static com.autobots.automanager.enumeracoes.PerfilUsuario.*;

import com.autobots.automanager.api.*;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.enumeracoes.PerfilUsuario;
import com.autobots.automanager.seguranca.Autenticacao.Identidade;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class Acesso implements HandlerInterceptor {
  private static final String ATRIBUTO = Acesso.class.getName();
  private static final Set<PerfilUsuario> PERFIS_GERENCIAVEIS = Set.of(GERENTE, VENDEDOR, CLIENTE);
  private final Autenticacao autenticacao;

  public Acesso(Autenticacao autenticacao) {
    this.autenticacao = autenticacao;
  }

  @Override
  public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
    if (!(handler instanceof HandlerMethod metodo)
        || metodo.getBeanType() == LoginControle.class
        || metodo.getBeanType() == org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController.class) {
      return true;
    }
    Identidade pessoa;
    try {
      pessoa = autenticacao.verificar(req.getHeader("Authorization"));
    } catch (ResponseStatusException e) {
      res.setHeader("WWW-Authenticate", "Bearer");
      throw e;
    }
    req.setAttribute(ATRIBUTO, pessoa);
    Class<?> tipo = metodo.getBeanType();
    String operacao = metodo.getMethod().getName();
    boolean leitura = operacao.equals("obter") || operacao.equals("listar");
    boolean equipe = pessoa.tem(GERENTE) || pessoa.tem(VENDEDOR);
    if (pessoa.tem(ADMINISTRADOR)
        || (tipo == UsuarioControle.class && (equipe || (pessoa.tem(CLIENTE) && leitura)))
        || ((tipo == ServicoControle.class || tipo == MercadoriaControle.class)
            && (pessoa.tem(GERENTE) || (pessoa.tem(VENDEDOR) && leitura)))
        || (tipo == VendaControle.class && (pessoa.tem(GERENTE)
            || (pessoa.tem(VENDEDOR) && (leitura || operacao.equals("criar")))
            || (pessoa.tem(CLIENTE) && leitura)))) {
      return true;
    }
    throw proibido();
  }

  public boolean podeConsultar(Usuario usuario) {
    Identidade pessoa = atual();
    return pessoa == null || podeGerenciar(pessoa, usuario)
        || (pessoa.tem(CLIENTE) && pessoa.id().equals(usuario.getId()));
  }

  public void exigirGestao(Usuario usuario) {
    Identidade pessoa = atual();
    if (pessoa != null && !podeGerenciar(pessoa, usuario)) {
      throw proibido();
    }
  }

  private boolean podeGerenciar(Identidade pessoa, Usuario usuario) {
    Set<PerfilUsuario> perfis = usuario.getPerfis();
    return pessoa.tem(ADMINISTRADOR)
        || (pessoa.tem(GERENTE) && !perfis.isEmpty() && PERFIS_GERENCIAVEIS.containsAll(perfis))
        || (pessoa.tem(VENDEDOR) && perfis.equals(Set.of(CLIENTE)));
  }

  public boolean podeConsultar(Venda venda) {
    Identidade pessoa = atual();
    return pessoa == null || pessoa.tem(ADMINISTRADOR) || pessoa.tem(GERENTE)
        || (pessoa.tem(VENDEDOR) && pessoa.id().equals(venda.getFuncionario().getId()))
        || (pessoa.tem(CLIENTE) && pessoa.id().equals(venda.getCliente().getId()));
  }

  public void exigirAutoria(Venda venda) {
    Identidade pessoa = atual();
    if (pessoa != null && !pessoa.tem(ADMINISTRADOR) && !pessoa.tem(GERENTE)
        && !pessoa.id().equals(venda.getFuncionario().getId())) {
      throw proibido();
    }
  }

  public ResponseStatusException proibido() {
    return new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
  }

  private Identidade atual() {
    if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes contexto) {
      return (Identidade) contexto.getRequest().getAttribute(ATRIBUTO);
    }
    return null;
  }
}
