package com.autobots.automanager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.autobots.automanager.repositorios.ClienteRepositorio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import com.autobots.automanager.seguranca.Autenticacao;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest(properties = "AUTOBOTS_ADMIN_PASSWORD=autobots-teste-local")
@AutoConfigureMockMvc
class AutomanagerApplicationTests {
  @Autowired WebApplicationContext contexto;
  @Autowired Autenticacao autenticacao;
  private MockMvc mvc;

  @org.junit.jupiter.api.BeforeEach
  void autenticarAdministrador() {
    Object token = autenticacao.entrar("admin", "autobots-teste-local").get("token");
    mvc = webAppContextSetup(contexto)
        .defaultRequest(get("/").header("Authorization", "Bearer " + token)).build();
  }
  @Autowired private ClienteRepositorio clientes;

  @Test
  void contextLoads() {}

  @Test
  void consultaRecursosDoCliente() throws Exception {
    long id = clientes.findAll().get(0).getId();
    mvc.perform(get("/documento/cliente/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].numero").value("1500"));
    mvc.perform(get("/telefone/cliente/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].numero").value("981234576"));
    mvc.perform(get("/endereco/cliente/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.cidade").value("Rio de Janeiro"));
  }

  @Test
  void clienteInexistenteRetorna404() throws Exception {
    for (String recurso : new String[] {"documento", "telefone", "endereco"}) {
      mvc.perform(get("/" + recurso + "/cliente/" + Long.MAX_VALUE))
          .andExpect(status().isNotFound());
    }
  }
}
