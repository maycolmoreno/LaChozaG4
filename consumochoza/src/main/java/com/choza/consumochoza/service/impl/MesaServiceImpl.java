package com.choza.consumochoza.service.impl;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.choza.consumochoza.modelo.dto.MesaDTO;
import com.choza.consumochoza.service.IMesaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MesaServiceImpl implements IMesaService {

	private final WebClient webClient;
	private static final String ENDPOINT = "/mesas";
	private static final Duration TIMEOUT = Duration.ofSeconds(10);

	@Override
	public List<MesaDTO> listarTodas() {
		try {
			return webClient.get().uri(ENDPOINT).retrieve()
					.bodyToMono(new ParameterizedTypeReference<List<MesaDTO>>() {})
					.block(TIMEOUT);
		} catch (Exception e) {
			log.error("Error al listar todas las mesas: {}", e.getMessage());
			return Collections.emptyList();
		}
	}

	@Override
	public List<MesaDTO> listarDisponibles() {
		try {
			return webClient.get().uri(ENDPOINT + "/disponibles").retrieve()
					.bodyToMono(new ParameterizedTypeReference<List<MesaDTO>>() {})
					.block(TIMEOUT);
		} catch (Exception e) {
			log.error("Error al listar mesas disponibles: {}", e.getMessage());
			return Collections.emptyList();
		}
	}

	@Override
	public List<MesaDTO> listarOcupadas() {
		try {
			return webClient.get().uri(ENDPOINT + "/ocupadas").retrieve()
					.bodyToMono(new ParameterizedTypeReference<List<MesaDTO>>() {})
					.block(TIMEOUT);
		} catch (Exception e) {
			log.error("Error al listar mesas ocupadas: {}", e.getMessage());
			return Collections.emptyList();
		}
	}

	@Override
	public MesaDTO obtenerPorId(int id) {
		return webClient.get().uri(ENDPOINT + "/{id}", id).retrieve()
				.bodyToMono(MesaDTO.class).block(TIMEOUT);
	}

	@Override
	public MesaDTO crear(MesaDTO mesa) {
		return webClient.post().uri(ENDPOINT).bodyValue(mesa).retrieve()
				.onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
						response -> response.bodyToMono(String.class).map(errorBody -> new RuntimeException(errorBody)))
				.bodyToMono(MesaDTO.class).block(TIMEOUT);
	}

	@Override
	public MesaDTO actualizar(int id, MesaDTO mesa) {
		return webClient.put().uri(ENDPOINT + "/{id}", id).bodyValue(mesa).retrieve()
				.onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
						response -> response.bodyToMono(String.class).map(errorBody -> new RuntimeException(errorBody)))
				.bodyToMono(MesaDTO.class).block(TIMEOUT);
	}

	@Override
	public void eliminar(int id) {
		webClient.delete().uri(ENDPOINT + "/{id}", id).retrieve()
				.toBodilessEntity().block(TIMEOUT);
	}
}
