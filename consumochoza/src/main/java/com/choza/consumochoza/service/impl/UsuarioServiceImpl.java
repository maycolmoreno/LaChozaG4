package com.choza.consumochoza.service.impl;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.choza.consumochoza.modelo.dto.UsuarioDTO;
import com.choza.consumochoza.service.IUsuarioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioServiceImpl implements IUsuarioService {

    private final WebClient webClient;
    private static final String ENDPOINT = "/usuarios";
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    @Override
    public boolean existeAlgunUsuario() {
        try {
            Boolean existe = webClient.get()
                    .uri(ENDPOINT + "/existe-alguno")
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block(TIMEOUT);
            return Boolean.TRUE.equals(existe);
        } catch (Exception e) {
            // Si hay error de conexión, asumimos que existen usuarios
            return true;
        }
    }

    @Override
    public List<UsuarioDTO> listarTodos() {
        try {
            return webClient.get()
                    .uri(ENDPOINT)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<UsuarioDTO>>() {})
                    .block(TIMEOUT);
        } catch (Exception e) {
            log.error("Error al listar usuarios: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public UsuarioDTO obtenerPorId(int id) {
        return webClient.get()
                .uri(ENDPOINT + "/{id}", id)
                .retrieve()
                .bodyToMono(UsuarioDTO.class)
                .block(TIMEOUT);
    }

    @Override
    public UsuarioDTO crear(UsuarioDTO usuario) {
        return webClient.post()
                .uri(ENDPOINT)
                .bodyValue(usuario)
                .retrieve()
                .onStatus(
                    status -> status.is4xxClientError() || status.is5xxServerError(),
                    response -> response.bodyToMono(String.class)
                        .map(errorBody -> new RuntimeException(errorBody))
                )
                .bodyToMono(UsuarioDTO.class)
                .block(TIMEOUT);
    }

    @Override
    public UsuarioDTO actualizar(int id, UsuarioDTO usuario) {
        return webClient.put()
                .uri(ENDPOINT + "/{id}", id)
                .bodyValue(usuario)
                .retrieve()
                .onStatus(
                    status -> status.is4xxClientError() || status.is5xxServerError(),
                    response -> response.bodyToMono(String.class)
                        .map(errorBody -> new RuntimeException(errorBody))
                )
                .bodyToMono(UsuarioDTO.class)
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
	
    @Override
    public UsuarioDTO crearAdminInicial(UsuarioDTO usuario) {
        return webClient.post()
                .uri(ENDPOINT + "/setup-admin")
                .bodyValue(usuario)
                .retrieve()
                .onStatus(
                    status -> status.is4xxClientError() || status.is5xxServerError(),
                    response -> response.bodyToMono(String.class)
                        .map(errorBody -> new RuntimeException(errorBody))
                )
                .bodyToMono(UsuarioDTO.class)
                .block(TIMEOUT);
    }

    @Override
    public UsuarioDTO buscarPorUsername(String username) {
        try {
            return webClient.get()
                    .uri(ENDPOINT + "/por-username/{username}", username)
                    .retrieve()
                    .bodyToMono(UsuarioDTO.class)
                    .block(TIMEOUT);
        } catch (Exception e) {
            log.error("Error al buscar usuario por username {}: {}", username, e.getMessage());
            return null;
        }
    }

    @Override
    public void cambiarPassword(String username, String passwordActual, String passwordNuevo) {
        var body = new java.util.HashMap<String, String>();
        body.put("passwordActual", passwordActual);
        body.put("passwordNuevo", passwordNuevo);
		
        webClient.post()
                .uri(ENDPOINT + "/cambiar-password")
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .block(TIMEOUT);
    }
}
