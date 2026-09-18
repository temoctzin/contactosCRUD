package com.examen.service;

import com.examen.dto.ContactoRequest;
import com.examen.dto.TelefonoDto;
import com.examen.model.Contacto;
import com.examen.model.Telefono;
import com.examen.repository.ContactoRepositorio;
import com.examen.repository.TelefonoRepositorio;
import com.examen.dto.ContactoResponse;
import com.examen.dto.ContactoRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
public class ContactoService {
	private final ContactoRepositorio contactoRepositorio;
	private final TelefonoRepositorio telefonoRepositorio;
	
	public ContactoService(ContactoRepositorio contactoRepositorio, TelefonoRepositorio telefonoRepositorio) {
		this.contactoRepositorio = contactoRepositorio;
		this.telefonoRepositorio = telefonoRepositorio;
	}
	
	@Transactional
	public ContactoResponse crear(ContactoRequest request) {
		if(contactoRepositorio.existsByEmail(request.email())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "El mail ya esta registrado");
		}
		
		Contacto contacto = new Contacto();
		contacto.setNombre(request.nombre());
		contacto.setApellidoPaterno(request.apellidoPaterno());
		contacto.setApellidoMaterno(request.apellidoMaterno());
		contacto.setEmail(request.email());
		contacto.setTelefonos(resolverTelefonos(request.telefonos()));
		
		return toResponse(contactoRepositorio.save(contacto));
	}
	
	private Set<Telefono> resolverTelefonos(Set<TelefonoDto> telefonosDto){
		Set<Telefono> telefonos = new HashSet<>();
		for(TelefonoDto dto : telefonosDto) {
			Telefono telefono = telefonoRepositorio.findByNumeroAndTipo(dto.numero(), dto.tipo())
					.orElseGet(() -> telefonoRepositorio.save(new Telefono(dto.numero(), dto.tipo())));
			telefonos.add(telefono);
		}
		return telefonos;
	}
	
	private ContactoResponse toResponse(Contacto contacto) {
		Set<TelefonoDto> telefonosDto = contacto.getTelefonos().stream()
				.map(p -> new TelefonoDto(p.getNumero(), p.getTipo()))
				.collect(Collectors.toSet());
		
		return new ContactoResponse(
				contacto.getId(),
				contacto.getNombre(),
				contacto.getApellidoPaterno(),
				contacto.getApellidoMaterno(),
				contacto.getEmail(),
				contacto.getFechaCreacion(),
				telefonosDto
				);
		
	}
	
}
