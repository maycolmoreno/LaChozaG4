package com.lachozag4.pisip.aplicacion.casosuso.resultado;

/**
 * Resultado inmutable que representa el resumen de ventas de un producto
 * en el contexto del reporte diario.
 */
public record ResumenProductoResultado(
		int idProducto,
		String nombreProducto,
		int cantidadVendida,
		double totalVendido) {
}
