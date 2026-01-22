
package com.lachozag4.pisip.dominio.entidades;

import java.io.Serializable;

import com.lachozag4.pisip.infraestructura.persistencia.jpa.PedidoJpa;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.ProductoJpa;


public class PedidoDetalle implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int idpedidodetalle;
    private final Integer cantidad;  // >= 1
    private ProductoJpa Fkproducto;
    private PedidoJpa Fkpedidodet;
	public PedidoDetalle(int idpedidodetalle, Integer cantidad, ProductoJpa fkproducto, PedidoJpa fkpedidodet) {
		
		this.idpedidodetalle = idpedidodetalle;
		this.cantidad = cantidad;
		Fkproducto = fkproducto;
		Fkpedidodet = fkpedidodet;
	}
	public ProductoJpa getFkproducto() {
		return Fkproducto;
	}
	public void setFkproducto(ProductoJpa fkproducto) {
		Fkproducto = fkproducto;
	}
	public PedidoJpa getFkpedidodet() {
		return Fkpedidodet;
	}
	public void setFkpedidodet(PedidoJpa fkpedidodet) {
		Fkpedidodet = fkpedidodet;
	}
	public int getIdpedidodetalle() {
		return idpedidodetalle;
	}
	public Integer getCantidad() {
		return cantidad;
	}
    
    
    
 }

