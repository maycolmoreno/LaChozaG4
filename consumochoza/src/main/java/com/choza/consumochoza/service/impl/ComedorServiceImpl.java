package com.choza.consumochoza.service.impl;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.choza.consumochoza.modelo.dto.ComedorDTO;
import com.choza.consumochoza.service.IComedorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComedorServiceImpl implements IComedorService {

	private final WebClient webClient;
	private static final String ENDPOINT = "/comedores";
	private static final Duration TIMEOUT = Duration.ofSeconds(10);

	@Override
	public List<ComedorDTO> listarTodos() {
		try {
			return webClient.get().uri(ENDPOINT).retrieve()
					.bodyToMono(new ParameterizedTypeReference<List<ComedorDTO>>() {})
					.block(TIMEOUT);
		} catch (Exception e) {
			log.error("Error al listar comedores: {}", e.getMessage());
			return Collections.emptyList();
		}
	}

	@Override
	public List<ComedorDTO> listarActivos() {
		try {
			return webClient.get().uri(ENDPOINT + "/activos").retrieve()
					.bodyToMono(new ParameterizedTypeReference<List<ComedorDTO>>() {})
					.block(TIMEOUT);
		} catch (Exception e) {
			log.error("Error al listar comedores activos: {}", e.getMessage());
			return Collections.emptyList();
		}
	}

	@Override
	public ComedorDTO obtenerPorId(int id) {
		return webClient.get().uri(ENDPOINT + "/{id}", id).retrieve()
				.bodyToMono(ComedorDTO.class).block(TIMEOUT);
	}

	@Override
	public ComedorDTO crear(ComedorDTO comedor) {
		return webClient.post().uri(ENDPOINT).bodyValue(comedor).retrieve()
				.onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
						response -> response.bodyToMono(String.class).map(errorBody -> new RuntimeException(errorBody)))
				.bodyToMono(ComedorDTO.class).block(TIMEOUT);
	}

	@Override
	public ComedorDTO actualizar(int id, ComedorDTO comedor) {
		return webClient.put().uri(ENDPOINT + "/{id}", id).bodyValue(comedor).retrieve()
				.onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
						response -> response.bodyToMono(String.class).map(errorBody -> new RuntimeException(errorBody)))
				.bodyToMono(ComedorDTO.class).block(TIMEOUT);
	}

	@Override
	public void eliminar(int id) {
		webClient.delete().uri(ENDPOINT + "/{id}", id).retrieve()
				.toBodilessEntity().block(TIMEOUT);
	}
}
