
package com.lachozag4.pisip.dominio.entidades;

import java.io.Serializable;
import java.time.LocalDateTime;


public class Pedido implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int idpedido;
    private final LocalDateTime fecha = LocalDateTime.now();
    private final boolean estado;
    private final String observaciones;
	public Pedido(int idpedido, boolean estado, String observaciones) {
		this.idpedido = idpedido;
		this.estado = estado;
		this.observaciones = observaciones;
	}
	public int getIdpedido() {
		return idpedido;
	}
	public LocalDateTime getFecha() {
		return fecha;
	}
	public boolean isEstado() {
		return estado;
	}
	public String getObservaciones() {
		return observaciones;
	}
	
    
    
    
	
}
