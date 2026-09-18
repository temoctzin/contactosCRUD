package com.examen.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.examen.model.Contacto;

public interface ContactoRepositorio extends JpaRepository<Contacto, Long> {
	boolean existsByEmail(String email);
	boolean existsByEmailAndIdNot(String email, Long id);
}
