package com.examen.cotroller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.examen.dto.ContactoRequest;
import com.examen.dto.ContactoResponse;
import com.examen.service.ContactoService;

@RestController
@RequestMapping("/contactos")
public class ContactoController {
	
	private final ContactoService contactoService;
	
	public ContactoController(ContactoService contactoService) {
		this.contactoService = contactoService;
	}
	
	@PostMapping
	public ResponseEntity<ContactoResponse> create(@Valid @RequestBody ContactoRequest request){
		return ResponseEntity.status(HttpStatus.CREATED).body(contactoService.crear(request));
	}

}
