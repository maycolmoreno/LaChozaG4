package com.choza.consumochoza.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.choza.consumochoza.modelo.dto.CuentaDTO;
import com.choza.consumochoza.service.ICuentaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CuentaServiceImpl implements ICuentaService {

    private final WebClient webClient;
    private static final String ENDPOINT = "/cuentas";

    @Override
    public List<CuentaDTO> listarTodas() {
        return webClient.get()
                .uri(ENDPOINT)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<CuentaDTO>>() {})
                .block();
    }

    @Override
    public List<CuentaDTO> listarAbiertas() {
        return webClient.get()
                .uri(ENDPOINT + "/abiertas")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<CuentaDTO>>() {})
                .block();
    }

    @Override
    public CuentaDTO obtenerPorId(int id) {
        return webClient.get()
                .uri(ENDPOINT + "/{id}", id)
                .retrieve()
                .bodyToMono(CuentaDTO.class)
                .block();
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
                .block();
    }

    @Override
    public CuentaDTO agregarPedido(int idCuenta, int idPedido) {
        return webClient.post()
                .uri(ENDPOINT + "/{idCuenta}/pedidos/{idPedido}", idCuenta, idPedido)
                .retrieve()
                .bodyToMono(CuentaDTO.class)
                .block();
    }

    @Override
    public CuentaDTO cambiarEstado(int idCuenta, String estado) {
        java.util.Map<String, Object> body = java.util.Map.of("estado", estado);
        return webClient.patch()
                .uri(ENDPOINT + "/{id}/estado", idCuenta)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(CuentaDTO.class)
                .block();
    }
}
