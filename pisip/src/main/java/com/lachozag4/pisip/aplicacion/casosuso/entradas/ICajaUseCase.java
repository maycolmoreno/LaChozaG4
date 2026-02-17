package com.lachozag4.pisip.aplicacion.casosuso.entradas;

import java.util.List;

import com.lachozag4.pisip.dominio.entidades.CajaTurno;

public interface ICajaUseCase {

	CajaTurno abrirCaja(double montoInicial, String usuarioApertura, String observaciones);

	CajaTurno obtenerCajaAbierta();

	CajaTurno cerrarCaja(double montoDeclaradoCierre, String usuarioCierre, String observaciones);

	List<CajaTurno> listar();
}
