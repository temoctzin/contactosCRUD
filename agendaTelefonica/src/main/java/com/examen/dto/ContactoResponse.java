package com.examen.dto;

import java.time.LocalDateTime;
import java.util.Set;

import com.examen.dto.TelefonoDto;

public record ContactoResponse(
		Long id,
		String nombre,
		String apellidoPaterno,
		String apellidoMaterno,
		String email,
		LocalDateTime fechaCreacion,
		Set<TelefonoDto> telefonos) {}
