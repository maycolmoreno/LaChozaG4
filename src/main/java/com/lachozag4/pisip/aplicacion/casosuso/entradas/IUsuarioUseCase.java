package com.lachozag4.pisip.aplicacion.casosuso.entradas;

import java.util.List;
import java.util.Optional;

import com.lachozag4.pisip.dominio.entidades.Usuario;

public interface IUsuarioUseCase {

	 Usuario crear(Usuario usuario);

	    Usuario obtenerPorId(int idusuario);

	    List<Usuario> listar();

	    Usuario actualizar(int idusuario, Usuario usuario);

	    void eliminar(int idusuario);
	    
	    Optional<Usuario> autenticar(String username, String password);
	    
	    Optional<Usuario> buscarPorUsername(String username);
	    
	    void cambiarPassword(String username, String passwordActual, String passwordNuevo);

}
