package com.lachozag4.pisip.dominio.repositorios;

import java.util.List;

import com.lachozag4.pisip.dominio.entidades.Pago;

public interface IPagoRepositorio {

	Pago guardar(Pago pago);

	List<Pago> listarPorCuenta(int idcuenta);

	double totalPagadoCuenta(int idcuenta);

	double totalPagadoCaja(int idcaja);
}
