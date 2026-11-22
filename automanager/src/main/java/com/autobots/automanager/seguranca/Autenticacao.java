package com.autobots.automanager.seguranca;

import com.autobots.automanager.entidades.CredencialUsuarioSenha;
import com.autobots.automanager.enumeracoes.PerfilUsuario;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Set;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class Autenticacao {
  public record Identidade(Long id, Set<PerfilUsuario> perfis) {
    public boolean tem(PerfilUsuario perfil) {
      return perfis.contains(perfil);
    }
  }

  private static final Base64.Encoder BASE64 = Base64.getUrlEncoder().withoutPadding();
  private static final String CABECALHO = BASE64.encodeToString(
      "{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
  private static final long DURACAO = 3600;
  private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
  private final String hashDeComparacao = bcrypt.encode("comparacao-sem-usuario");
  private final ObjectMapper json;
  private final EntityManager em;
  private final SecretKeySpec chave;

  public Autenticacao(ObjectMapper json, EntityManager em, @Value("${AUTOBOTS_JWT_SECRET:}") String segredo) {
    this.json = json;
    this.em = em;
    byte[] bytes = segredo.getBytes(StandardCharsets.UTF_8);
    if (bytes.length == 0) {
      bytes = new byte[32];
      new SecureRandom().nextBytes(bytes);
    } else if (bytes.length < 32) {
      throw new IllegalArgumentException("AUTOBOTS_JWT_SECRET deve conter pelo menos 32 bytes");
    }
    this.chave = new SecretKeySpec(bytes, "HmacSHA256");
  }

  @Transactional(readOnly = true)
  public Map<String, Object> entrar(String nomeUsuario, String senha) {
    if (nomeUsuario == null || senha == null || senha.getBytes(StandardCharsets.UTF_8).length > 72) {
      throw naoAutenticado();
    }
    CredencialUsuarioSenha credencial = em.createQuery(
            "select c from CredencialUsuarioSenha c where c.nomeUsuario = :nome",
            CredencialUsuarioSenha.class)
        .setParameter("nome", nomeUsuario)
        .getResultStream().findFirst().orElse(null);
    String hash = credencial == null ? hashDeComparacao : credencial.getSenha();
    if (!bcrypt.matches(senha, hash) || credencial == null || credencial.isInativo()) {
      throw naoAutenticado();
    }
    try {
      String carga = BASE64.encodeToString(json.writeValueAsBytes(Map.of(
          "iss", "autobots", "sub", credencial.getId().toString(),
          "ver", credencial.getVersao(), "exp", Instant.now().getEpochSecond() + DURACAO)));
      String dados = CABECALHO + "." + carga;
      return Map.of("token", dados + "." + BASE64.encodeToString(assinar(dados)),
          "tipo", "Bearer", "expiraEm", DURACAO);
    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }

  @Transactional(readOnly = true)
  public Identidade verificar(String autorizacao) {
    if (autorizacao == null || autorizacao.length() > 2048
        || !autorizacao.regionMatches(true, 0, "Bearer ", 0, 7)) {
      throw naoAutenticado();
    }
    String[] partes = autorizacao.substring(7).split("\\.", -1);
    if (partes.length != 3 || !CABECALHO.equals(partes[0])) {
      throw naoAutenticado();
    }
    JsonNode carga;
    long id;
    try {
      byte[] assinatura = Base64.getUrlDecoder().decode(partes[2]);
      if (!partes[2].equals(BASE64.encodeToString(assinatura))
          || !MessageDigest.isEqual(assinar(partes[0] + "." + partes[1]), assinatura)) {
        throw naoAutenticado();
      }
      carga = json.readTree(Base64.getUrlDecoder().decode(partes[1]));
      id = Long.parseLong(carga.path("sub").asText());
    } catch (java.io.IOException | IllegalArgumentException e) {
      throw naoAutenticado();
    }
    if (!"autobots".equals(carga.path("iss").asText())
        || carga.path("exp").asLong() <= Instant.now().getEpochSecond()) {
      throw naoAutenticado();
    }
    CredencialUsuarioSenha credencial = em.find(CredencialUsuarioSenha.class, id);
    if (credencial == null || credencial.isInativo()
        || credencial.getVersao() != carga.path("ver").asLong(-1)) {
      throw naoAutenticado();
    }
    return new Identidade(credencial.getUsuario().getId(),
        Set.copyOf(credencial.getUsuario().getPerfis()));
  }

  private byte[] assinar(String dados) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(chave);
      return mac.doFinal(dados.getBytes(StandardCharsets.US_ASCII));
    } catch (GeneralSecurityException e) {
      throw new IllegalStateException(e);
    }
  }

  static ResponseStatusException naoAutenticado() {
    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credencial ou token invalido");
  }
}
