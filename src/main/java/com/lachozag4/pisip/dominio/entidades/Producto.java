package com.lachozag4.pisip.dominio.entidades;

import java.io.Serializable;

public class Producto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int idproducto;
	private final String nombre;
	private final Double precio;
	private final Integer stockActual;
	private final String descripcion;
	private final boolean estado;

	public Producto(int idproducto, String nombre, Double precio, Integer stockActual, String descripcion,
			boolean estado) {
		this.idproducto = idproducto;
		this.nombre = nombre;
		this.precio = precio;
		this.stockActual = stockActual;
		this.descripcion = descripcion;
		this.estado = estado;
	}

	public int getIdproducto() {
		return idproducto;
	}

	public String getNombre() {
		return nombre;
	}

	public Double getPrecio() {
		return precio;
	}

	public Integer getStockActual() {
		return stockActual;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public boolean isEstado() {
		return estado;
	}

}