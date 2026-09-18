package com.examen.cotroller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.examen.dto.ContactoRequest;
import com.examen.dto.ContactoResponse;
import com.examen.service.ContactoService;

import java.util.List;

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
	
	@GetMapping("/{id}")
	public ResponseEntity<ContactoResponse> getById(@PathVariable Long id){
		return ResponseEntity.ok(contactoService.getById(id));
	}
	
	@GetMapping
    public ResponseEntity<List<ContactoResponse>> getAll() {
        return ResponseEntity.ok(contactoService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactoResponse> update(
        @PathVariable Long id,
        @Valid @RequestBody ContactoRequest request
    ) {
        return ResponseEntity.ok(contactoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
    	contactoService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
