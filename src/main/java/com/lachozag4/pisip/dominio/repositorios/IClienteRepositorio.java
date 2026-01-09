package com.lachozag4.pisip.dominio.repositorios;

import java.util.List;
import java.util.Optional;


import com.lachozag4.pisip.dominio.entidades.Cliente;

public interface IClienteRepositorio {
	Cliente guardar(Cliente cliente);
	Optional<Cliente> BuscarPorId(int id);
	List<Cliente> listarTodos();
	void eliminar (int id);

}
