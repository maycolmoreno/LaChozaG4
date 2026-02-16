package com.lachozag4.pisip.aplicacion.casosuso.impl;

import java.util.List;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IMesaUseCase;
import com.lachozag4.pisip.aplicacion.excepciones.BusinessException;
import com.lachozag4.pisip.aplicacion.excepciones.NotFoundException;
import com.lachozag4.pisip.dominio.entidades.Mesa;
import com.lachozag4.pisip.dominio.repositorios.IMesaRepositorio;

public class MesaUseCaseImpl implements IMesaUseCase {

	private final IMesaRepositorio repositorio;

	public MesaUseCaseImpl(IMesaRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public Mesa crear(Mesa mesa) {
		// Validar número único
		repositorio.buscarPorNumero(mesa.getNumero()).ifPresent(existente -> {
			throw new BusinessException("Ya existe una mesa con el número: " + mesa.getNumero());
		});
		return repositorio.guardar(mesa);
	}

	@Override
	public Mesa obtenerPorId(int idmesa) {
		return repositorio.buscarPorId(idmesa)
				.orElseThrow(() -> new NotFoundException("Mesa no encontrada con ID: " + idmesa));
	}

	@Override
	public List<Mesa> listar() {
		// Listar todas las mesas activas (independiente de si tienen pedidos)
		return repositorio.listarTodas();
	}

	@Override
	public void eliminar(int idmesa) {
		Mesa mesa = obtenerPorId(idmesa);
		// Eliminación lógica: desactivar en vez de borrar físicamente
		Mesa desactivada = new Mesa(
				mesa.getIdmesa(),
				mesa.getNumero(),
				mesa.getCapacidad(),
				false
		);
		repositorio.guardar(desactivada);
	}

	@Override
	public Mesa actualizar(int idmesa, Mesa mesa) {
		obtenerPorId(idmesa); // Valida existencia

		// Validar número único (excluir la propia mesa)
		repositorio.buscarPorNumero(mesa.getNumero()).ifPresent(existente -> {
			if (existente.getIdmesa() != idmesa) {
				throw new BusinessException("Ya existe una mesa con el número: " + mesa.getNumero());
			}
		});

		var mesaActualizada = new Mesa(idmesa, mesa.getNumero(), mesa.getCapacidad(), mesa.getEstado());
		return repositorio.guardar(mesaActualizada);
	}

	@Override
	public List<Mesa> listarDisponibles() {
		// Ahora "disponibles" significa mesas activas, sin considerar pedidos activos
		return repositorio.listarDisponibles();
	}

	@Override
	public List<Mesa> listarOcupadas() {
		return repositorio.listarMesasOcupadas();
	}

}
