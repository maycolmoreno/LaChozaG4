package com.choza.consumochoza.service.impl;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.choza.consumochoza.modelo.dto.CategoriaDTO;
import com.choza.consumochoza.service.ICategoriaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements ICategoriaService {

	private final WebClient webClient;
	private static final String ENDPOINT = "/categorias";

	@Override
	public List<CategoriaDTO> listarTodas() {
		return webClient.get().uri(ENDPOINT).retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<CategoriaDTO>>() {
				}).block();
	}

	@Override
	public List<CategoriaDTO> listarActivas() {
		return webClient.get().uri(ENDPOINT + "/activas").retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<CategoriaDTO>>() {
				}).block();
	}

	@Override
	public CategoriaDTO obtenerPorId(int id) {
		return webClient.get().uri(ENDPOINT + "/{id}", id).retrieve().bodyToMono(CategoriaDTO.class).block();
	}

	@Override
	public CategoriaDTO crear(CategoriaDTO categoria) {
		return webClient.post().uri(ENDPOINT).bodyValue(categoria).retrieve()
				.onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
						response -> response.bodyToMono(String.class).map(errorBody -> new RuntimeException(errorBody)))
				.bodyToMono(CategoriaDTO.class).block();
	}

	@Override
	public CategoriaDTO actualizar(int id, CategoriaDTO categoria) {
		return webClient.put().uri(ENDPOINT + "/{id}", id).bodyValue(categoria).retrieve()
				.onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
						response -> response.bodyToMono(String.class).map(errorBody -> new RuntimeException(errorBody)))
				.bodyToMono(CategoriaDTO.class).block();
	}

	@Override
	public void eliminar(int id) {
		webClient.delete().uri(ENDPOINT + "/{id}", id).retrieve().toBodilessEntity().block();
	}
}
