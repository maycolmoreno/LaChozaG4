package com.choza.consumochoza.service.impl;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.choza.consumochoza.modelo.dto.PagoDTO;
import com.choza.consumochoza.service.IPagoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PagoServiceImpl implements IPagoService {

    private final WebClient webClient;
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    @Override
    public PagoDTO registrarPago(int idCuenta, double monto, String metodo, String referencia, String usuario) {
        Map<String, Object> body = Map.of(
                "monto", monto,
                "metodo", metodo,
                "referencia", referencia == null ? "" : referencia,
                "usuario", usuario
        );

        return webClient.post()
                .uri("/cuentas/{idCuenta}/pagos", idCuenta)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(PagoDTO.class)
                .block(TIMEOUT);
    }

    @Override
    public List<PagoDTO> listarPorCuenta(int idCuenta) {
        try {
            return webClient.get()
                    .uri("/cuentas/{idCuenta}/pagos", idCuenta)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<PagoDTO>>() {})
                    .block(TIMEOUT);
        } catch (Exception e) {
            log.error("Error al listar pagos de cuenta {}: {}", idCuenta, e.getMessage());
            return Collections.emptyList();
        }
    }
}
