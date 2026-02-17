package com.lachozag4.pisip.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.lachozag4.pisip.dominio.entidades.Cliente;

public interface IClienteRepositorio {
	Cliente guardar(Cliente cliente);
	Optional<Cliente> buscarPorId(int idcliente);
	Optional<Cliente> buscarPorCedula(String cedula);
	Optional<Cliente> buscarPorEmail(String email);
	List<Cliente> listarActivos();
	List<Cliente> listarTodos();
	Cliente actualizar(Cliente cliente);
	boolean existePorId(int idcliente);
	void eliminar(int idcliente);
}
