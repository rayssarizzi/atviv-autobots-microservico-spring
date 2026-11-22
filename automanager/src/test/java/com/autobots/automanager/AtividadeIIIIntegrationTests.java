package com.autobots.automanager;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.autobots.automanager.entidades.*;
import com.autobots.automanager.repositorios.*;
import com.fasterxml.jackson.databind.*;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import com.autobots.automanager.seguranca.Autenticacao;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest(properties = "AUTOBOTS_ADMIN_PASSWORD=autobots-teste-local")
@AutoConfigureMockMvc
class AtividadeIIIIntegrationTests {
  @Autowired WebApplicationContext contexto;
  @Autowired Autenticacao autenticacao;
  private MockMvc mvc;

  @org.junit.jupiter.api.BeforeEach
  void autenticarAdministrador() {
    Object token = autenticacao.entrar("admin", "autobots-teste-local").get("token");
    mvc = webAppContextSetup(contexto)
        .defaultRequest(get("/").header("Authorization", "Bearer " + token)).build();
  }
  @Autowired ObjectMapper json;
  @Autowired CredencialRepositorio credenciais;

  private String unico() {
    return UUID.randomUUID().toString();
  }

  private Map<String, Object> ref(long id) {
    return Map.of("id", id);
  }

  private JsonNode criar(String recurso, Object corpo) throws Exception {
    String resposta =
        mvc.perform(
                post("/api/" + recurso)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.writeValueAsString(corpo)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$._links.self.href").exists())
            .andReturn()
            .getResponse()
            .getContentAsString();
    return json.readTree(resposta);
  }

  private long empresa() throws Exception {
    return criar("empresas", Map.of("razaoSocial", "Oficina " + unico())).get("id").asLong();
  }

  private long usuario(long empresa, String perfil) throws Exception {
    return criar(
            "usuarios",
            Map.of("nome", "Pessoa " + unico(), "empresa", ref(empresa), "perfis", List.of(perfil)))
        .get("id")
        .asLong();
  }

  private Map<String, Object> corpo(String recurso) throws Exception {
    Map<String, Object> m = new LinkedHashMap<>();
    switch (recurso) {
      case "clientes":
        m.put("nome", "Cliente anterior");
        break;
      case "empresas":
        m.put("razaoSocial", "Oficina " + unico());
        break;
      case "usuarios":
        m.putAll(Map.of("nome", "Ana", "empresa", ref(empresa()), "perfis", List.of("CLIENTE")));
        break;
      case "veiculos":
        m.putAll(
            Map.of(
                "tipo",
                "HATCH",
                "modelo",
                "Teste",
                "placa",
                placa(),
                "proprietario",
                ref(usuario(empresa(), "CLIENTE"))));
        break;
      case "mercadorias":
        m.putAll(
            Map.of(
                "nome",
                "Filtro",
                "empresa",
                ref(empresa()),
                "validade",
                "2099-01-01",
                "fabricacao",
                "2025-01-01",
                "quantidade",
                10,
                "valor",
                50));
        break;
      case "servicos":
        m.putAll(Map.of("nome", "Revisão", "empresa", ref(empresa()), "valor", 100));
        break;
      case "emails":
        m.put("endereco", "teste@example.com");
        break;
      case "telefones":
        m.putAll(Map.of("ddd", "11", "numero", "999999999"));
        break;
      case "enderecos":
        m.putAll(
            Map.of(
                "estado",
                "SP",
                "cidade",
                "São Paulo",
                "bairro",
                "Centro",
                "rua",
                "Rua A",
                "numero",
                "1",
                "codigoPostal",
                "01001000"));
        break;
      case "documentos":
        m.putAll(Map.of("tipo", "RG", "numero", unico(), "dataEmissao", "2020-01-01"));
        break;
      case "credenciais":
        m.putAll(
            Map.of(
                "tipo",
                "USUARIO_SENHA",
                "usuario",
                ref(usuario(empresa(), "CLIENTE")),
                "nomeUsuario",
                unico(),
                "senha",
                "SenhaFicticia123!"));
        break;
      case "vendas":
        return venda();
      default:
        throw new IllegalArgumentException(recurso);
    }
    return m;
  }

  private static int sequencia;

  private String placa() {
    int n = ++sequencia;
    return "TST" + String.format("%04d", n);
  }

  private Map<String, Object> venda() throws Exception {
    long e = empresa(), c = usuario(e, "CLIENTE"), f = usuario(e, "FUNCIONARIO");
    long v =
        criar(
                "veiculos",
                Map.of("tipo", "SUV", "modelo", "Teste", "placa", placa(), "proprietario", ref(c)))
            .get("id")
            .asLong();
    long m =
        criar(
                "mercadorias",
                Map.of(
                    "nome",
                    "Peça",
                    "empresa",
                    ref(e),
                    "validade",
                    "2099-01-01",
                    "fabricacao",
                    "2025-01-01",
                    "quantidade",
                    10,
                    "valor",
                    50))
            .get("id")
            .asLong();
    long s =
        criar("servicos", Map.of("nome", "Troca", "empresa", ref(e), "valor", 100))
            .get("id")
            .asLong();
    return new LinkedHashMap<>(
        Map.of(
            "identificacao",
            unico(),
            "empresa",
            ref(e),
            "cliente",
            ref(c),
            "funcionario",
            ref(f),
            "veiculo",
            ref(v),
            "mercadorias",
            List.of(Map.of("mercadoria", ref(m), "quantidade", 2)),
            "servicos",
            List.of(Map.of("servico", ref(s), "quantidade", 1))));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
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
        "telefones",
        "enderecos"
      })
  void crudCompletoComHateoas(String recurso) throws Exception {
    Map<String, Object> corpo = corpo(recurso);
    JsonNode criado = criar(recurso, corpo);
    long id = criado.get("id").asLong();
    mvc.perform(get("/api/" + recurso + "/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$._links.colecao.href").exists());
    mvc.perform(get("/api/" + recurso))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._links.self.href").exists());
    if (corpo.containsKey("nome")) corpo.put("nome", "Atualizado");
    if (recurso.equals("empresas")) corpo.put("razaoSocial", "Atualizada");
    mvc.perform(
            put("/api/" + recurso + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(corpo)))
        .andExpect(status().isOk());
    mvc.perform(delete("/api/" + recurso + "/" + id)).andExpect(status().isNoContent());
    mvc.perform(get("/api/" + recurso + "/" + id)).andExpect(status().isNotFound());
  }

  @Test
  void rejeitaDadosInvalidosEReferenciasAusentes() throws Exception {
    mvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isBadRequest());
    mvc.perform(
            post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    json.writeValueAsString(
                        Map.of(
                            "nome",
                            "Ana",
                            "perfis",
                            List.of("CLIENTE"),
                            "empresa",
                            ref(Long.MAX_VALUE)))))
        .andExpect(status().isNotFound());
    mvc.perform(get("/api/empresas/abc")).andExpect(status().isBadRequest());
    mvc.perform(
            post("/api/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipo\":\"INVALIDO\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void linksDeAssociacaoSaoNavegaveis() throws Exception {
    long e = empresa(), u = usuario(e, "CLIENTE");
    JsonNode emp =
        json.readTree(
            mvc.perform(get("/api/empresas/" + e)).andReturn().getResponse().getContentAsString());
    for (String rel : List.of("usuarios", "mercadorias", "servicos", "vendas"))
      mvc.perform(get(emp.path("_links").path(rel).path("href").asText()))
          .andExpect(status().isOk());
    JsonNode user =
        json.readTree(
            mvc.perform(get("/api/usuarios/" + u)).andReturn().getResponse().getContentAsString());
    for (String rel : List.of("empresa", "veiculos", "credenciais", "vendas"))
      mvc.perform(get(user.path("_links").path(rel).path("href").asText()))
          .andExpect(status().isOk());
    mvc.perform(get("/api"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._links.empresas.href").exists());
  }

  @Test
  void senhaEHashNaoSaoExpostos() throws Exception {
    JsonNode c = criar("credenciais", corpo("credenciais"));
    assertFalse(c.has("senha"));
    CredencialUsuarioSenha entidade =
        (CredencialUsuarioSenha) credenciais.findById(c.get("id").asLong()).orElseThrow();
    assertTrue(new BCryptPasswordEncoder().matches("SenhaFicticia123!", entidade.getSenha()));
    String resposta =
        mvc.perform(get("/api/credenciais/" + c.get("id").asLong()))
            .andReturn()
            .getResponse()
            .getContentAsString();
    assertFalse(resposta.contains("SenhaFicticia"));
    assertFalse(resposta.contains(entidade.getSenha()));
  }

  @Test
  void credencialCodigoBarraTemCrud() throws Exception {
    Map<String, Object> corpo =
        new LinkedHashMap<>(
            Map.of(
                "tipo",
                "CODIGO_BARRA",
                "usuario",
                ref(usuario(empresa(), "CLIENTE")),
                "codigo",
                unico()));
    JsonNode c = criar("credenciais", corpo);
    assertFalse(c.has("codigo"));
    long id = c.get("id").asLong();
    corpo.put("inativo", true);
    mvc.perform(
            put("/api/credenciais/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(corpo)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.inativo").value(true));
    mvc.perform(delete("/api/credenciais/" + id)).andExpect(status().isNoContent());
  }

  @Test
  void estoqueTotalHistoricoEExclusaoDaVenda() throws Exception {
    Map<String, Object> venda = venda();
    JsonNode criado = criar("vendas", venda);
    long id = criado.get("id").asLong();
    assertEquals(0, new BigDecimal("200").compareTo(criado.get("total").decimalValue()));
    long m = criado.at("/mercadorias/0/mercadoria/id").asLong();
    mvc.perform(get("/api/mercadorias/" + m)).andExpect(jsonPath("$.quantidade").value(8));
    JsonNode atual =
        json.readTree(
            mvc.perform(get("/api/mercadorias/" + m))
                .andReturn()
                .getResponse()
                .getContentAsString());
    Map<String, Object> editar = json.convertValue(atual, Map.class);
    editar.put("valor", 999);
    mvc.perform(
            put("/api/mercadorias/" + m)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(editar)))
        .andExpect(status().isOk());
    mvc.perform(
            put("/api/vendas/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(venda)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.total").value(200));
    mvc.perform(get("/api/mercadorias/" + m)).andExpect(jsonPath("$.quantidade").value(8));
    mvc.perform(delete("/api/vendas/" + id)).andExpect(status().isNoContent());
    mvc.perform(get("/api/mercadorias/" + m)).andExpect(jsonPath("$.quantidade").value(10));
  }

  @Test
  void falhaNaVendaNaoAlteraEstoque() throws Exception {
    Map<String, Object> venda = venda();
    long m =
        ((Number)
                ((Map<?, ?>)
                        ((Map<?, ?>) ((List<?>) venda.get("mercadorias")).get(0)).get("mercadoria"))
                    .get("id"))
            .longValue();
    venda.put("mercadorias", List.of(Map.of("mercadoria", ref(m), "quantidade", 1000)));
    mvc.perform(
            post("/api/vendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(venda)))
        .andExpect(status().isBadRequest());
    mvc.perform(get("/api/mercadorias/" + m)).andExpect(jsonPath("$.quantidade").value(10));
  }

  @Test
  void rejeitaVendasEntreEmpresasEExclusaoDeVinculoEmUso() throws Exception {
    Map<String, Object> venda = venda();
    venda.put("cliente", ref(usuario(empresa(), "CLIENTE")));
    mvc.perform(
            post("/api/vendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(venda)))
        .andExpect(status().isBadRequest());
    long e = empresa();
    usuario(e, "CLIENTE");
    mvc.perform(delete("/api/empresas/" + e)).andExpect(status().isConflict());
    mvc.perform(get("/api/empresas/" + e)).andExpect(status().isOk());
  }

  @Test
  void falhaAposBaixaReverteTodaTransacao() throws Exception {
    Map<String, Object> v = venda();
    long m =
        ((Number)
                ((Map<?, ?>)
                        ((Map<?, ?>) ((List<?>) v.get("mercadorias")).get(0)).get("mercadoria"))
                    .get("id"))
            .longValue();
    v.put("servicos", List.of(Map.of("servico", ref(Long.MAX_VALUE), "quantidade", 1)));
    mvc.perform(
            post("/api/vendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(v)))
        .andExpect(status().isNotFound());
    mvc.perform(get("/api/mercadorias/" + m)).andExpect(jsonPath("$.quantidade").value(10));
  }

  @Test
  void perfilEmUsoNaoPodeSerRemovido() throws Exception {
    Map<String, Object> v = venda();
    criar("vendas", v);
    long c = ((Number) ((Map<?, ?>) v.get("cliente")).get("id")).longValue();
    Map<String, Object> alteracao =
        Map.of("nome", "Ana", "empresa", v.get("empresa"), "perfis", List.of("FORNECEDOR"));
    mvc.perform(
            put("/api/usuarios/" + c)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(alteracao)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void camposNegativosDuplicadosESenhaCurtaSaoRejeitados() throws Exception {
    Map<String, Object> m = corpo("mercadorias");
    m.put("valor", -1);
    mvc.perform(
            post("/api/mercadorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(m)))
        .andExpect(status().isBadRequest());
    Map<String, Object> c = corpo("credenciais");
    c.put("senha", "123");
    mvc.perform(
            post("/api/credenciais")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(c)))
        .andExpect(status().isBadRequest());
    Map<String, Object> v = corpo("veiculos");
    criar("veiculos", v);
    mvc.perform(
            post("/api/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(v)))
        .andExpect(status().isConflict());
  }

  @Test
  void credenciaisDuplicadasRetornamConflito() throws Exception {
    Map<String, Object> c = corpo("credenciais");
    criar("credenciais", c);
    mvc.perform(
            post("/api/credenciais")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(c)))
        .andExpect(status().isConflict());
  }

  private String cadastrarLogin(long usuario) throws Exception {
    String nome = unico();
    criar("credenciais", Map.of("tipo", "USUARIO_SENHA", "usuario", ref(usuario),
        "nomeUsuario", nome, "senha", "senha-local-de-teste"));
    String resposta = mvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(Map.of("nomeUsuario", nome, "senha", "senha-local-de-teste"))))
        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    return "Bearer " + json.readTree(resposta).get("token").asText();
  }

  @Test
  void exigeTokenValidoInclusiveNasRotasAntigas() throws Exception {
    MockMvc anonimo = webAppContextSetup(contexto).build();
    anonimo.perform(get("/api/usuarios")).andExpect(status().isUnauthorized());
    anonimo.perform(get("/documento/cliente/1")).andExpect(status().isUnauthorized());
    anonimo.perform(get("/api/vendas").header("Authorization", "Bearer jwt-invalido"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void aplicaPermissoesPorPerfilEPorVenda() throws Exception {
    Map<String, Object> corpo = venda();
    long empresa = ((Number) ((Map<?, ?>) corpo.get("empresa")).get("id")).longValue();
    long cliente = ((Number) ((Map<?, ?>) corpo.get("cliente")).get("id")).longValue();
    long vendedor = usuario(empresa, "VENDEDOR");
    long outroVendedor = usuario(empresa, "VENDEDOR");
    long gerente = usuario(empresa, "GERENTE");
    String tokenCliente = cadastrarLogin(cliente);
    String tokenVendedor = cadastrarLogin(vendedor);
    String tokenOutro = cadastrarLogin(outroVendedor);
    String tokenGerente = cadastrarLogin(gerente);

    mvc.perform(get("/api/usuarios/" + cliente).header("Authorization", tokenCliente))
        .andExpect(status().isOk());
    mvc.perform(get("/api/usuarios/" + gerente).header("Authorization", tokenCliente))
        .andExpect(status().isForbidden());
    mvc.perform(get("/api/usuarios/" + vendedor).header("Authorization", tokenGerente))
        .andExpect(status().isOk());
    mvc.perform(get("/api/usuarios/" + gerente).header("Authorization", tokenVendedor))
        .andExpect(status().isForbidden());
    mvc.perform(post("/api/usuarios").header("Authorization", tokenGerente)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(Map.of("nome", "Elevacao", "empresa", ref(empresa),
                "perfis", List.of("ADMINISTRADOR")))))
        .andExpect(status().isForbidden());

    corpo.put("funcionario", ref(vendedor));
    String entrada = json.writeValueAsString(corpo);
    mvc.perform(post("/api/vendas").header("Authorization", tokenOutro)
            .contentType(MediaType.APPLICATION_JSON).content(entrada))
        .andExpect(status().isForbidden());
    String local = mvc.perform(post("/api/vendas").header("Authorization", tokenVendedor)
            .contentType(MediaType.APPLICATION_JSON).content(entrada))
        .andExpect(status().isCreated()).andReturn().getResponse().getHeader("Location");
    String rota = java.net.URI.create(local).getPath();
    mvc.perform(get(rota).header("Authorization", tokenCliente)).andExpect(status().isOk());
    mvc.perform(get(rota).header("Authorization", tokenVendedor)).andExpect(status().isOk());
    mvc.perform(get(rota).header("Authorization", tokenOutro)).andExpect(status().isForbidden());
    mvc.perform(get("/api/vendas").header("Authorization", tokenOutro))
        .andExpect(status().isOk()).andExpect(jsonPath("$._embedded").doesNotExist());
    mvc.perform(delete(rota).header("Authorization", tokenVendedor)).andExpect(status().isForbidden());
    mvc.perform(delete(rota).header("Authorization", tokenGerente)).andExpect(status().isNoContent());
  }

}

