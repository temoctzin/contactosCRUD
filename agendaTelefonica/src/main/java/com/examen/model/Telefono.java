package com.examen.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import java.util.Set;
import java.util.HashSet;

@Entity
public class Telefono {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 20)
	private String numero;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private TipoNumeroTelefonico tipo;
	
	@ManyToMany(mappedBy = "telefonos")
	private Set<Contacto> contactos = new HashSet<>();
	
	public Telefono() {}
	
	public Telefono(String numero, TipoNumeroTelefonico tipo) {
		this.numero = numero;
		this.tipo = tipo;
	}

	public Long getId() {
		return id;
	}


	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public TipoNumeroTelefonico getTipo() {
		return tipo;
	}

	public void setTipo(TipoNumeroTelefonico tipo) {
		this.tipo = tipo;
	}

	public Set<Contacto> getContactos() {
		return contactos;
	}

	public void setContactos(Set<Contacto> contactos) {
		this.contactos = contactos;
	}
	
	
}
