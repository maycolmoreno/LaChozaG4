package com.choza.consumochoza.service.impl;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.choza.consumochoza.modelo.dto.ProductoDTO;
import com.choza.consumochoza.service.IProductoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoServiceImpl implements IProductoService {

	private final WebClient webClient;
	private static final String ENDPOINT = "/productos";
	private static final Duration TIMEOUT = Duration.ofSeconds(10);

	@Override
	public List<ProductoDTO> listarTodos() {
		try {
			return webClient.get().uri(ENDPOINT).retrieve()
					.bodyToMono(new ParameterizedTypeReference<List<ProductoDTO>>() {})
					.block(TIMEOUT);
		} catch (Exception e) {
			log.error("Error al listar productos: {}", e.getMessage());
			return Collections.emptyList();
		}
	}

	@Override
	public List<ProductoDTO> listarActivos() {
		try {
			return webClient.get().uri(ENDPOINT + "/activos").retrieve()
					.bodyToMono(new ParameterizedTypeReference<List<ProductoDTO>>() {})
					.block(TIMEOUT);
		} catch (Exception e) {
			log.error("Error al listar productos activos: {}", e.getMessage());
			return Collections.emptyList();
		}
	}

	@Override
	public List<ProductoDTO> listarPorCategoria(int idCategoria) {
		try {
			return webClient.get().uri(ENDPOINT + "/categoria/{id}", idCategoria).retrieve()
					.bodyToMono(new ParameterizedTypeReference<List<ProductoDTO>>() {})
					.block(TIMEOUT);
		} catch (Exception e) {
			log.error("Error al listar productos por categoría {}: {}", idCategoria, e.getMessage());
			return Collections.emptyList();
		}
	}

	@Override
	public ProductoDTO obtenerPorId(int id) {
		return webClient.get().uri(ENDPOINT + "/{id}", id).retrieve()
				.bodyToMono(ProductoDTO.class).block(TIMEOUT);
	}

	@Override
	public ProductoDTO crear(ProductoDTO producto) {
		return webClient.post().uri(ENDPOINT).bodyValue(producto).retrieve()
				.onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
						response -> response.bodyToMono(String.class).map(errorBody -> new RuntimeException(errorBody)))
				.bodyToMono(ProductoDTO.class).block(TIMEOUT);
	}

	@Override
	public ProductoDTO actualizar(int id, ProductoDTO producto) {
		return webClient.put().uri(ENDPOINT + "/{id}", id).bodyValue(producto).retrieve()
				.onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
						response -> response.bodyToMono(String.class).map(errorBody -> new RuntimeException(errorBody)))
				.bodyToMono(ProductoDTO.class).block(TIMEOUT);
	}

	@Override
	public void eliminar(int id) {
		webClient.delete().uri(ENDPOINT + "/{id}", id).retrieve()
				.toBodilessEntity().block(TIMEOUT);
	}
}
