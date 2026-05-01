package com.choza.consumochoza.service.impl;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.choza.consumochoza.modelo.dto.ClienteDTO;
import com.choza.consumochoza.service.IClienteService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteServiceImpl implements IClienteService {

    private final WebClient webClient;
    private static final String ENDPOINT = "/clientes";
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    @Override
    public List<ClienteDTO> listarTodos() {
        try {
            return webClient.get()
                    .uri(ENDPOINT)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<ClienteDTO>>() {})
                    .block(TIMEOUT);
        } catch (Exception e) {
            log.error("Error al listar clientes: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public ClienteDTO obtenerPorId(int id) {
        return webClient.get()
                .uri(ENDPOINT + "/{id}", id)
                .retrieve()
                .bodyToMono(ClienteDTO.class)
                .block(TIMEOUT);
    }

    @Override
    public ClienteDTO crear(ClienteDTO cliente) {
        return webClient.post()
                .uri(ENDPOINT)
                .bodyValue(cliente)
                .retrieve()
                .onStatus(
                    status -> status.is4xxClientError() || status.is5xxServerError(),
                    response -> response.bodyToMono(String.class)
                        .map(errorBody -> new RuntimeException(errorBody))
                )
                .bodyToMono(ClienteDTO.class)
                .block(TIMEOUT);
    }

    @Override
    public ClienteDTO actualizar(int id, ClienteDTO cliente) {
        return webClient.put()
                .uri(ENDPOINT + "/{id}", id)
                .bodyValue(cliente)
                .retrieve()
                .onStatus(
                    status -> status.is4xxClientError() || status.is5xxServerError(),
                    response -> response.bodyToMono(String.class)
                        .map(errorBody -> new RuntimeException(errorBody))
                )
                .bodyToMono(ClienteDTO.class)
                .block(TIMEOUT);
    }

    @Override
    public void eliminar(int id) {
        webClient.delete()
                .uri(ENDPOINT + "/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .block(TIMEOUT);
    }
}
