package com.examen.repository;

import com.examen.model.Telefono;
import com.examen.model.TipoNumeroTelefonico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TelefonoRepositorio extends JpaRepository<Telefono, Long> {
	Optional<Telefono> findByNumeroAndTipo(String nombre, TipoNumeroTelefonico tipo);
}
