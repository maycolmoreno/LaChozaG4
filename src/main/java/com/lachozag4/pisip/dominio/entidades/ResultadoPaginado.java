package com.lachozag4.pisip.dominio.entidades;

import java.util.List;

/**
 * Wrapper genérico para resultados paginados en la capa de dominio.
 * Evita depender de Spring Data Page en el dominio (arquitectura hexagonal).
 */
public class ResultadoPaginado<T> {

	private final List<T> contenido;
	private final long totalElementos;
	private final int totalPaginas;
	private final int paginaActual;
	private final int tamanioPagina;

	public ResultadoPaginado(List<T> contenido, long totalElementos, int totalPaginas,
							 int paginaActual, int tamanioPagina) {
		this.contenido = contenido;
		this.totalElementos = totalElementos;
		this.totalPaginas = totalPaginas;
		this.paginaActual = paginaActual;
		this.tamanioPagina = tamanioPagina;
	}

	public List<T> getContenido() {
		return contenido;
	}

	public long getTotalElementos() {
		return totalElementos;
	}

	public int getTotalPaginas() {
		return totalPaginas;
	}

	public int getPaginaActual() {
		return paginaActual;
	}

	public int getTamanioPagina() {
		return tamanioPagina;
	}
}
