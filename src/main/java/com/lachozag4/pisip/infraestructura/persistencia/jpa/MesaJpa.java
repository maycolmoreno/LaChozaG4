package com.lachozag4.pisip.infraestructura.persistencia.jpa;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="mesa")
@Data
public class MesaJpa implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idmesa;
	private Integer numero;
	private Integer capacidad;
	private boolean estado; // "LIBRE", "OCUPADA"
	private String ubicacion; // "Salon 1", "Salon 2"

}
