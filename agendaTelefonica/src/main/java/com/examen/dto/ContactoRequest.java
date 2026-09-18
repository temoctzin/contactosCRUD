package com.examen.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

public record ContactoRequest(
		@NotBlank(message = "El nombre es obligatorio")
		String nombre,
		
		@NotBlank(message = "El apellido es obligatorio")
		String apellidoPaterno,
		
		@NotBlank(message = "El apellido es obligatorio")
		String apellidoMaterno,
		
		@Email(message = "Formato de email inválido")
		@NotBlank(message = "El email es obligatorio")
		String email,
		
		@NotEmpty(message = "Debe registrar al menos un teléfono")
		@Valid
		Set<TelefonoDto> telefonos
		
		
		) {}
