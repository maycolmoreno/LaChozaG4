package com.choza.consumochoza.service.impl;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.choza.consumochoza.modelo.dto.MesaDTO;
import com.choza.consumochoza.service.IMesaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MesaServiceImpl implements IMesaService {

	private final WebClient webClient;
	private static final String ENDPOINT = "/mesas";

	@Override
	public List<MesaDTO> listarTodas() {
		return webClient.get().uri(ENDPOINT).retrieve().bodyToMono(new ParameterizedTypeReference<List<MesaDTO>>() {
		}).block();
	}

	@Override
	public List<MesaDTO> listarDisponibles() {
		return webClient.get().uri(ENDPOINT + "/disponibles").retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<MesaDTO>>() {
				}).block();
	}

	@Override
	public List<MesaDTO> listarOcupadas() {
		return webClient.get().uri(ENDPOINT + "/ocupadas").retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<MesaDTO>>() {
				}).block();
	}

	@Override
	public MesaDTO obtenerPorId(int id) {
		return webClient.get().uri(ENDPOINT + "/{id}", id).retrieve().bodyToMono(MesaDTO.class).block();
	}

	@Override
	public MesaDTO crear(MesaDTO mesa) {
		return webClient.post().uri(ENDPOINT).bodyValue(mesa).retrieve()
				.onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
						response -> response.bodyToMono(String.class).map(errorBody -> new RuntimeException(errorBody)))
				.bodyToMono(MesaDTO.class).block();
	}

	@Override
	public MesaDTO actualizar(int id, MesaDTO mesa) {
		return webClient.put().uri(ENDPOINT + "/{id}", id).bodyValue(mesa).retrieve()
				.onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
						response -> response.bodyToMono(String.class).map(errorBody -> new RuntimeException(errorBody)))
				.bodyToMono(MesaDTO.class).block();
	}

	@Override
	public void eliminar(int id) {
		webClient.delete().uri(ENDPOINT + "/{id}", id).retrieve().toBodilessEntity().block();
	}
}
