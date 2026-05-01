package com.choza.consumochoza.service.impl;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.choza.consumochoza.modelo.dto.CuentaDTO;
import com.choza.consumochoza.service.ICuentaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CuentaServiceImpl implements ICuentaService {

    private final WebClient webClient;
    private static final String ENDPOINT = "/cuentas";
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    @Override
    public List<CuentaDTO> listarTodas() {
        try {
            return webClient.get()
                    .uri(ENDPOINT)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<CuentaDTO>>() {})
                    .block(TIMEOUT);
        } catch (Exception e) {
            log.error("Error al listar cuentas: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<CuentaDTO> listarAbiertas() {
        try {
            return webClient.get()
                    .uri(ENDPOINT + "/abiertas")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<CuentaDTO>>() {})
                    .block(TIMEOUT);
        } catch (Exception e) {
            log.error("Error al listar cuentas abiertas: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public CuentaDTO obtenerPorId(int id) {
        return webClient.get()
                .uri(ENDPOINT + "/{id}", id)
                .retrieve()
                .bodyToMono(CuentaDTO.class)
                .block(TIMEOUT);
    }

    @Override
    public CuentaDTO crearCuenta(int idMesa, int idCliente) {
        Map<String, Object> body = Map.of(
                "idMesa", idMesa,
                "idCliente", idCliente,
                "total", 0.0
        );

        return webClient.post()
                .uri(ENDPOINT)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(CuentaDTO.class)
                .block(TIMEOUT);
    }

    @Override
    public CuentaDTO agregarPedido(int idCuenta, int idPedido) {
        return webClient.post()
                .uri(ENDPOINT + "/{idCuenta}/pedidos/{idPedido}", idCuenta, idPedido)
                .retrieve()
                .bodyToMono(CuentaDTO.class)
                .block(TIMEOUT);
    }

    @Override
    public CuentaDTO cambiarEstado(int idCuenta, String estado) {
        java.util.Map<String, Object> body = java.util.Map.of("estado", estado);
        return webClient.patch()
                .uri(ENDPOINT + "/{id}/estado", idCuenta)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(CuentaDTO.class)
                .block(TIMEOUT);
    }

    @Override
    public CuentaDTO asignarCliente(int idCuenta, int idCliente) {
        java.util.Map<String, Object> body = java.util.Map.of("idCliente", idCliente);
        return webClient.patch()
                .uri(ENDPOINT + "/{id}/cliente", idCuenta)
                .bodyValue(body)
                .retrieve()
                .onStatus(
                    status -> status.is4xxClientError() || status.is5xxServerError(),
                    response -> response.bodyToMono(String.class)
                        .map(RuntimeException::new))
                .bodyToMono(CuentaDTO.class)
                .block(TIMEOUT);
    }
}
