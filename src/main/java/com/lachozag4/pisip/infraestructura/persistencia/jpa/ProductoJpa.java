package com.lachozag4.pisip.infraestructura.persistencia.jpa;

import java.io.Serializable;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="producto")
@Data
public class ProductoJpa implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idproducto;
    private String nombre;
    private Double precio;
    private Integer stockActual;
    private String descripcion;
    private Boolean activo;
    
}
