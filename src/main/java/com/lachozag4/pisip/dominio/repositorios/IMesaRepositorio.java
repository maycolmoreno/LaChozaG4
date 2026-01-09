package com.lachozag4.pisip.dominio.repositorios;

import java.util.List;
import java.util.Optional;


import com.lachozag4.pisip.dominio.entidades.Mesa;

public interface IMesaRepositorio {
	Mesa guardar(Mesa mesa);
	Optional<Mesa> BuscarPorId(int id);
	List<Mesa> listarTodos();
	void eliminar (int id);

}
