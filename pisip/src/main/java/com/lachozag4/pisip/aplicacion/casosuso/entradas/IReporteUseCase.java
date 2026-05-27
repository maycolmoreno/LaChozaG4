package com.lachozag4.pisip.aplicacion.casosuso.entradas;

import java.time.LocalDate;

import com.lachozag4.pisip.aplicacion.casosuso.resultado.ReporteVentasDiaResultado;

public interface IReporteUseCase {

	/**
	 * Calcula el reporte de ventas para una fecha dada.
	 * Solo consulta los pedidos COMPLETADOS de ese día en BD (sin cargar la tabla completa).
	 */
	ReporteVentasDiaResultado obtenerVentasDia(LocalDate fecha);
}
