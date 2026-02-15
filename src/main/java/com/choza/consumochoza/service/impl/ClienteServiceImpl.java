package com.choza.consumochoza.service.impl;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.choza.consumochoza.modelo.dto.ClienteDTO;
import com.choza.consumochoza.service.IClienteService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements IClienteService {

    private final WebClient webClient;
    private static final String ENDPOINT = "/clientes";

    @Override
    public List<ClienteDTO> listarTodos() {
        return webClient.get()
                .uri(ENDPOINT)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ClienteDTO>>() {})
                .block();
    }

    @Override
    public ClienteDTO obtenerPorId(int id) {
        return webClient.get()
                .uri(ENDPOINT + "/{id}", id)
                .retrieve()
                .bodyToMono(ClienteDTO.class)
                .block();
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
                .block();
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
                .block();
    }

    @Override
    public void eliminar(int id) {
        webClient.delete()
                .uri(ENDPOINT + "/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
