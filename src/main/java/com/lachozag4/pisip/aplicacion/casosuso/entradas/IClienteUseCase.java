package com.lachozag4.pisip.aplicacion.casosuso.entradas;

import java.util.List;

import com.lachozag4.pisip.dominio.entidades.Cliente;

public interface IClienteUseCase {
	
	Cliente crear(Cliente cliente);

	Cliente obtenerPorId(int id);

	List<Cliente> listar();

	void eliminar(int id);

}
