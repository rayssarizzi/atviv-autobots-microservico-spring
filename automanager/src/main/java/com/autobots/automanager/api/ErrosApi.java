package com.autobots.automanager.api;

import java.util.*;
import javax.persistence.OptimisticLockException;
import org.springframework.dao.*;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice(basePackages = "com.autobots.automanager.api")
@org.springframework.core.annotation.Order(0)
public class ErrosApi {
  @ExceptionHandler(ResponseStatusException.class)
  ResponseEntity<?> negocio(ResponseStatusException e) {
    return resposta(e.getStatus(), e.getReason());
  }

  @ExceptionHandler({
    DataIntegrityViolationException.class,
    OptimisticLockingFailureException.class,
    OptimisticLockException.class
  })
  ResponseEntity<?> conflito(Exception e) {
    return resposta(
        HttpStatus.CONFLICT, "Registro duplicado, vínculo em uso ou alteração concorrente");
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<?> invalido(MethodArgumentNotValidException e) {
    return resposta(
        HttpStatus.BAD_REQUEST,
        e.getBindingResult().getFieldErrors().stream()
            .map(f -> f.getField() + ": " + f.getDefaultMessage())
            .collect(java.util.stream.Collectors.joining("; ")));
  }

  @ExceptionHandler({
    HttpMessageNotReadableException.class,
    org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class
  })
  ResponseEntity<?> formato(Exception e) {
    return resposta(HttpStatus.BAD_REQUEST, "JSON, enumeração ou identificador inválido");
  }

  private ResponseEntity<?> resposta(HttpStatus status, String mensagem) {
    return ResponseEntity.status(status)
        .body(
            Map.of(
                "status",
                status.value(),
                "erro",
                mensagem == null ? status.getReasonPhrase() : mensagem));
  }
}
