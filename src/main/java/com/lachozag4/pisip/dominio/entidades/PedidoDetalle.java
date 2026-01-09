
package com.lachozag4.pisip.dominio.entidades;

import java.io.Serializable;


public class PedidoDetalle implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int idpedidodetalle;
    private final Integer cantidad;  // >= 1
   
	public PedidoDetalle(int idpedidodetalle, Integer cantidad) {
		super();
		this.idpedidodetalle = idpedidodetalle;
		this.cantidad = cantidad;
	}
	public int getIdpedidodetalle() {
		return idpedidodetalle;
	}
	public Integer getCantidad() {
		return cantidad;
	}
	   
    
 }

