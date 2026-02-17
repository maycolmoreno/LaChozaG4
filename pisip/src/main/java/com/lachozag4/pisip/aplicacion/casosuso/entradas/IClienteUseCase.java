package com.lachozag4.pisip.aplicacion.casosuso.entradas;

import java.util.List;

import com.lachozag4.pisip.dominio.entidades.Cliente;

public interface IClienteUseCase {
	
	Cliente crear(Cliente cliente);

	Cliente obtenerPorId(int idcliente);

	List<Cliente> listar();

	void eliminar(int idcliente);

	Cliente actualizar(int idcliente, Cliente cliente);

}
