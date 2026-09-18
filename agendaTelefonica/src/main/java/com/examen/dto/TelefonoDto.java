package com.examen.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.examen.model.TipoNumeroTelefonico;


public record TelefonoDto (
	@NotBlank(message = "El número no puede estar vacío")	
	String numero,
	
	@NotNull(message = "El tipo de telefono es obligatorio")
	TipoNumeroTelefonico tipo
){}
