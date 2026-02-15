package com.choza.consumochoza.service.impl;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.choza.consumochoza.modelo.dto.ProductoDTO;
import com.choza.consumochoza.service.IProductoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements IProductoService {

	private final WebClient webClient;
	private static final String ENDPOINT = "/productos";

	@Override
	public List<ProductoDTO> listarTodos() {
		return webClient.get().uri(ENDPOINT).retrieve().bodyToMono(new ParameterizedTypeReference<List<ProductoDTO>>() {
		}).block();
	}

	@Override
	public List<ProductoDTO> listarActivos() {
		return webClient.get().uri(ENDPOINT + "/activos").retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<ProductoDTO>>() {
				}).block();
	}

	@Override
	public List<ProductoDTO> listarPorCategoria(int idCategoria) {
		return webClient.get().uri(ENDPOINT + "/categoria/{id}", idCategoria).retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<ProductoDTO>>() {
				}).block();
	}

	@Override
	public ProductoDTO obtenerPorId(int id) {
		return webClient.get().uri(ENDPOINT + "/{id}", id).retrieve().bodyToMono(ProductoDTO.class).block();
	}

	@Override
	public ProductoDTO crear(ProductoDTO producto) {
		return webClient.post().uri(ENDPOINT).bodyValue(producto).retrieve()
				.onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
						response -> response.bodyToMono(String.class).map(errorBody -> new RuntimeException(errorBody)))
				.bodyToMono(ProductoDTO.class).block();
	}

	@Override
	public ProductoDTO actualizar(int id, ProductoDTO producto) {
		return webClient.put().uri(ENDPOINT + "/{id}", id).bodyValue(producto).retrieve()
				.onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
						response -> response.bodyToMono(String.class).map(errorBody -> new RuntimeException(errorBody)))
				.bodyToMono(ProductoDTO.class).block();
	}

	@Override
	public void eliminar(int id) {
		webClient.delete().uri(ENDPOINT + "/{id}", id).retrieve().toBodilessEntity().block();
	}
}
