package com.choza.consumochoza.service.impl;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.choza.consumochoza.modelo.dto.CajaTurnoDTO;
import com.choza.consumochoza.service.ICajaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CajaServiceImpl implements ICajaService {

    private final WebClient webClient;
    private static final String ENDPOINT = "/caja";
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    @Override
    public CajaTurnoDTO abrirCaja(double montoInicial, String usuarioApertura, String observaciones) {
        Map<String, Object> body = Map.of(
                "montoInicial", montoInicial,
                "usuarioApertura", usuarioApertura,
                "observaciones", observaciones == null ? "" : observaciones
        );

        return webClient.post()
                .uri(ENDPOINT + "/apertura")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(CajaTurnoDTO.class)
                .block(TIMEOUT);
    }

    @Override
    public CajaTurnoDTO obtenerCajaAbierta() {
        return webClient.get()
                .uri(ENDPOINT + "/abierta")
                .retrieve()
                .bodyToMono(CajaTurnoDTO.class)
                .block(TIMEOUT);
    }

    @Override
    public CajaTurnoDTO cerrarCaja(double montoDeclaradoCierre, String usuarioCierre, String observaciones) {
        Map<String, Object> body = Map.of(
                "montoDeclaradoCierre", montoDeclaradoCierre,
                "usuarioCierre", usuarioCierre,
                "observaciones", observaciones == null ? "" : observaciones
        );

        return webClient.post()
                .uri(ENDPOINT + "/cierre")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(CajaTurnoDTO.class)
                .block(TIMEOUT);
    }

    @Override
    public List<CajaTurnoDTO> listarCajas() {
        try {
            return webClient.get()
                    .uri(ENDPOINT)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<CajaTurnoDTO>>() {})
                    .block(TIMEOUT);
        } catch (Exception e) {
            log.error("Error al listar cajas: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
