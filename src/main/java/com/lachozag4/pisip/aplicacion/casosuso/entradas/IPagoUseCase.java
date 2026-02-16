package com.lachozag4.pisip.aplicacion.casosuso.entradas;

import java.util.List;

import com.lachozag4.pisip.dominio.entidades.Pago;

public interface IPagoUseCase {

	Pago registrarPago(int idcuenta, double monto, String metodo, String referencia, String usuario);

	List<Pago> listarPorCuenta(int idcuenta);

	double totalPagadoCuenta(int idcuenta);

	double saldoPendienteCuenta(int idcuenta);
}
