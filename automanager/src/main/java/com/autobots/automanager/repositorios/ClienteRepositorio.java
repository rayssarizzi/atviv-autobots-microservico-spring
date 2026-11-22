package com.autobots.automanager.repositorios;

import com.autobots.automanager.entidades.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepositorio extends JpaRepository<Cliente, Long> {}
