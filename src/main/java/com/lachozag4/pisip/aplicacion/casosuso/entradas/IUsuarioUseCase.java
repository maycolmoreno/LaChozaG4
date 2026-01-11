package com.lachozag4.pisip.aplicacion.casosuso.entradas;

import java.util.List;

import com.lachozag4.pisip.dominio.entidades.Usuario;

public interface IUsuarioUseCase {

	Usuario crear(Usuario usuario);

	Usuario obtenerPorId(int id);

	List<Usuario> listar();

	void eliminar(int id);

}
