package com.lachozag4.pisip.infraestructura.persistencia.jpa;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="pedido")
@Data
public class PedidoJpa implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idpedido;
    private LocalDateTime fecha = LocalDateTime.now();
    private boolean estado;
    private String observaciones;            

}
