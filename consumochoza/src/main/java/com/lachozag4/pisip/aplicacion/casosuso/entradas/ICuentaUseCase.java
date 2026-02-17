package com.lachozag4.pisip.aplicacion.casosuso.entradas;

import java.util.List;

import com.lachozag4.pisip.dominio.entidades.Cuenta;

public interface ICuentaUseCase {

	Cuenta crear(Cuenta cuenta);

	Cuenta obtenerPorId(int idcuenta);

	List<Cuenta> listar();

	List<Cuenta> listarAbiertas();

	Cuenta cambiarEstado(int idcuenta, String nuevoEstado);

	void eliminar(int idcuenta);

	/**
	 * Agrega un pedido existente a una cuenta y recalcula el total.
	 */
	Cuenta agregarPedido(int idcuenta, int idpedido);
}
