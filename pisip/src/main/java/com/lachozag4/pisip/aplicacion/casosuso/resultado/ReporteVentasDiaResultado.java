package com.lachozag4.pisip.aplicacion.casosuso.resultado;

import java.time.LocalDate;
import java.util.List;

import com.lachozag4.pisip.dominio.entidades.Pedido;

/**
 * Resultado inmutable del reporte de ventas de un día.
 * Contiene entidades de dominio; el controlador convierte a DTOs de respuesta.
 */
public record ReporteVentasDiaResultado(
		LocalDate fecha,
		double totalVentas,
		int numeroPedidos,
		double ticketPromedio,
		double totalEfectivo,
		double totalTarjeta,
		double totalTransferencias,
		double totalOtros,
		int totalProductos,
		List<Pedido> pedidos,
		List<ResumenProductoResultado> productosMasVendidos) {
}
