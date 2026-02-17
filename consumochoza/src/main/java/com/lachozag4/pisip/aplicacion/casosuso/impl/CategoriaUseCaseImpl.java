package com.lachozag4.pisip.aplicacion.casosuso.impl;

import java.util.List;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.ICategoriaUseCase;
import com.lachozag4.pisip.aplicacion.excepciones.NotFoundException;
import com.lachozag4.pisip.dominio.entidades.Categoria;
import com.lachozag4.pisip.dominio.repositorios.ICategoriaRepositorio;

public class CategoriaUseCaseImpl implements ICategoriaUseCase {

	private final ICategoriaRepositorio categoriaRepositorio;

	public CategoriaUseCaseImpl(ICategoriaRepositorio categoriaRepositorio) {
		this.categoriaRepositorio = categoriaRepositorio;
	}

	@Override
	public Categoria crear(Categoria categoria) {
		// Validar nombre único
		return categoriaRepositorio.guardar(categoria);
	}

	@Override
	public Categoria buscarPorId(int id) {
		return categoriaRepositorio.buscarPorId(id)
				.orElseThrow(() -> new NotFoundException("Categoria no encontrada con id: " + id));
	}

	@Override
	public List<Categoria> listarTodos() {
		return categoriaRepositorio.listarTodas();
	}

	@Override
	public List<Categoria> listarActivas() {
		return categoriaRepositorio.listarActivas();
	}

	@Override
	public Categoria actualizar(int id, Categoria categoria) {
		// Verificar que exista (lanzará NotFoundException si no existe)
		buscarPorId(id);
		// Construir nueva instancia con el id del path y los nuevos datos
		Categoria actualizada = new Categoria(
				id,
				categoria.getNombre(),
				categoria.getDescripcion(),
				categoria.getEstado());
		return categoriaRepositorio.actualizar(actualizada);
	}

	@Override
	public void eliminar(int id) {
		Categoria categoria = buscarPorId(id);
		// Eliminación lógica: desactivar en vez de borrar físicamente
		Categoria desactivada = new Categoria(categoria.getIdcategoria(), categoria.getNombre(),
				categoria.getDescripcion(), false);
		categoriaRepositorio.guardar(desactivada);
	}
}
