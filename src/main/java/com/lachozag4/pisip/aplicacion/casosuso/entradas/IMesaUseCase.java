package com.lachozag4.pisip.aplicacion.casosuso.entradas;

import java.util.List;

import com.lachozag4.pisip.dominio.entidades.Mesa;

public interface IMesaUseCase {

	Mesa crear(Mesa mesa);

	Mesa obtenerPorId(int id);

	List<Mesa> listar();

	void eliminar(int id);

}
