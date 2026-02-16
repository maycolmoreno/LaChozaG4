package com.lachozag4.pisip.dominio.entidades;

import java.io.Serializable;

public class Producto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final int idproducto;
    private final String nombre;
    private final double precio;
    private final int stockActual;
    private final String descripcion;
    private final String imagenUrl;
    private final boolean estado;
	private Categoria fkCategoria;

	
	
	public Producto(int idproducto, String nombre, double precio, int stockActual, String descripcion, String imagenUrl,
			boolean estado, Categoria fkCategoria) {
		this.idproducto = idproducto;
		this.nombre = nombre;
		this.precio = precio;
		this.stockActual = stockActual;
		this.descripcion = descripcion;
		this.imagenUrl = imagenUrl;
		this.estado = estado;
		this.fkCategoria = fkCategoria;
	}

	public int getIdproducto() {
		return idproducto;
	}

	public String getNombre() {
		return nombre;
	}

	public double getPrecio() {
		return precio;
	}

	public int getStockActual() {
		return stockActual;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public String getImagenUrl() {
		return imagenUrl;
	}

	public boolean getEstado() {
		return estado;
	}

	

    public boolean estaActivo() {
        return estado;
    }
    

    public Categoria getFkCategoria() {
		return fkCategoria;
	}

	public void setFkCategoria(Categoria fkCategoria) {
		this.fkCategoria = fkCategoria;
	}

	/**
     * Verifica si hay stock suficiente para la cantidad solicitada
     */
    public boolean hayStockDisponible(int cantidadRequerida) {
        return stockActual >= cantidadRequerida;
    }

    /**
     * Retorna nueva instancia con stock descontado (inmutabilidad)
     */
    public Producto descontarStock(int cantidad) {
        if (!hayStockDisponible(cantidad)) {
            throw new IllegalArgumentException(
                "Stock insuficiente para " + nombre + ". Disponible: " + stockActual + ", Solicitado: " + cantidad);
        }
        return new Producto(idproducto, nombre, precio, stockActual - cantidad, 
                descripcion, imagenUrl, estado, fkCategoria);
    }

    /**
     * Retorna nueva instancia con stock restaurado (para cancelaciones)
     */
    public Producto restaurarStock(int cantidad) {
        return new Producto(idproducto, nombre, precio, stockActual + cantidad, 
                descripcion, imagenUrl, estado, fkCategoria);
    }

    @Override
    public String toString() {
        return "Producto{" +
                "idproducto=" + idproducto +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", stock=" + stockActual +
                ", categoria=" + (fkCategoria != null ? fkCategoria.getNombre() : "Sin categoría") +
                '}';
    }
}