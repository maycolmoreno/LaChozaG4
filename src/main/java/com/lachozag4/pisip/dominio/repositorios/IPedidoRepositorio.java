package com.lachozag4.pisip.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.lachozag4.pisip.dominio.entidades.Pedido;


public interface IPedidoRepositorio {
	Pedido guardar(Pedido pedido);
	Optional<Pedido> BuscarPorId(int id);
	List<Pedido> listarTodos();
	void eliminar (int id);

}
