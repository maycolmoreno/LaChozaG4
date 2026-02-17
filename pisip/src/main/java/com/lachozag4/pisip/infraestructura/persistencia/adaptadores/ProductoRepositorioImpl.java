package com.lachozag4.pisip.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.lachozag4.pisip.dominio.entidades.Producto;
import com.lachozag4.pisip.dominio.repositorios.IProductoRepositorio;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.IProductoJpaMapper;
import com.lachozag4.pisip.infraestructura.repositorios.IProductoJpaRepositorio;

public class ProductoRepositorioImpl implements IProductoRepositorio {

	private final IProductoJpaRepositorio jpaRepositorio;
	private final IProductoJpaMapper mapper;

	public ProductoRepositorioImpl(IProductoJpaRepositorio jpaRepositorio, IProductoJpaMapper mapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.mapper = mapper;
	}

	@Override
	public Producto guardar(Producto producto) {
		return mapper.toDomain(jpaRepositorio.save(mapper.toEntity(producto)));
	}

	@Override
	public Optional<Producto> buscarPorId(int id) {
		return jpaRepositorio.findById(id).map(mapper::toDomain);
	}

	@Override
	public Optional<Producto> buscarPorNombreYCategoria(String nombre, int idCategoria) {
		return jpaRepositorio.findByNombreAndFkCategoriaId_Idcategoria(nombre, idCategoria).map(mapper::toDomain);
	}

	@Override
	public List<Producto> listarTodos() {
		return jpaRepositorio.findAll().stream().map(mapper::toDomain).toList();
	}

	@Override
	public List<Producto> listarActivos() {
		return jpaRepositorio.findByEstadoAndStockActualGreaterThan(true, 0).stream().map(mapper::toDomain).toList();
	}

	@Override
	public List<Producto> listarPorCategoria(int idCategoria) {
		return jpaRepositorio.findByFkCategoriaId_Idcategoria(idCategoria).stream().map(mapper::toDomain).toList();
	}

	@Override
	public Producto actualizar(Producto producto) {
		return guardar(producto);
	}

	@Override
	public boolean existePorId(int id) {
		return jpaRepositorio.existsById(id);
	}

	@Override
	public void eliminar(int id) {
		if (existePorId(id)) {
			jpaRepositorio.deleteById(id);
		}
	}
}