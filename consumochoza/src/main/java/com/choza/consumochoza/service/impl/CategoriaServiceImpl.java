package com.choza.consumochoza.service.impl;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.choza.consumochoza.modelo.dto.CategoriaDTO;
import com.choza.consumochoza.service.ICategoriaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoriaServiceImpl implements ICategoriaService {

	private final WebClient webClient;
	private static final String ENDPOINT = "/categorias";
	private static final Duration TIMEOUT = Duration.ofSeconds(10);

	@Override
	public List<CategoriaDTO> listarTodas() {
		try {
			return webClient.get().uri(ENDPOINT).retrieve()
					.bodyToMono(new ParameterizedTypeReference<List<CategoriaDTO>>() {})
					.block(TIMEOUT);
		} catch (Exception e) {
			log.error("Error al listar categorías: {}", e.getMessage());
			return Collections.emptyList();
		}
	}

	@Override
	public List<CategoriaDTO> listarActivas() {
		try {
			return webClient.get().uri(ENDPOINT + "/activas").retrieve()
					.bodyToMono(new ParameterizedTypeReference<List<CategoriaDTO>>() {})
					.block(TIMEOUT);
		} catch (Exception e) {
			log.error("Error al listar categorías activas: {}", e.getMessage());
			return Collections.emptyList();
		}
	}

	@Override
	public CategoriaDTO obtenerPorId(int id) {
		return webClient.get().uri(ENDPOINT + "/{id}", id).retrieve()
				.bodyToMono(CategoriaDTO.class).block(TIMEOUT);
	}

	@Override
	public CategoriaDTO crear(CategoriaDTO categoria) {
		return webClient.post().uri(ENDPOINT).bodyValue(categoria).retrieve()
				.onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
						response -> response.bodyToMono(String.class).map(errorBody -> new RuntimeException(errorBody)))
				.bodyToMono(CategoriaDTO.class).block(TIMEOUT);
	}

	@Override
	public CategoriaDTO actualizar(int id, CategoriaDTO categoria) {
		return webClient.put().uri(ENDPOINT + "/{id}", id).bodyValue(categoria).retrieve()
				.onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
						response -> response.bodyToMono(String.class).map(errorBody -> new RuntimeException(errorBody)))
				.bodyToMono(CategoriaDTO.class).block(TIMEOUT);
	}

	@Override
	public void eliminar(int id) {
		webClient.delete().uri(ENDPOINT + "/{id}", id).retrieve()
				.toBodilessEntity().block(TIMEOUT);
	}
}
