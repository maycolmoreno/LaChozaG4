package com.choza.consumochoza.service.impl;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.multipart.MultipartFile;

import com.choza.consumochoza.modelo.dto.ComprobanteDTO;
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

    @Override
    public ComprobanteDTO subirComprobante(int idCuenta, int idPago, MultipartFile archivo, String usuario) {
        try {
            MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
            bodyBuilder.part("archivo", new NamedByteArrayResource(archivo.getBytes(), archivo.getOriginalFilename()))
                    .contentType(resolveMediaType(archivo));
            bodyBuilder.part("usuario", usuario);

            return webClient.post()
                    .uri("/cuentas/{idCuenta}/pagos/{idPago}/comprobante", idCuenta, idPago)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(bodyBuilder.build())
                    .retrieve()
                    .bodyToMono(ComprobanteDTO.class)
                    .block(TIMEOUT);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer el archivo del comprobante.", e);
        }
    }

    @Override
    public ComprobanteDTO obtenerComprobante(int idCuenta, int idPago) {
        try {
            return webClient.get()
                    .uri("/cuentas/{idCuenta}/pagos/{idPago}/comprobante", idCuenta, idPago)
                    .retrieve()
                    .bodyToMono(ComprobanteDTO.class)
                    .block(TIMEOUT);
        } catch (WebClientResponseException ex) {
            if (ex.getStatusCode().value() == 404) {
                return null;
            }
            throw ex;
        }
    }

    private static MediaType resolveMediaType(MultipartFile archivo) {
        String contentType = archivo.getContentType();
        if (contentType == null || contentType.isBlank()) {
            return MediaType.IMAGE_JPEG;
        }
        return MediaType.parseMediaType(contentType);
    }

    private static final class NamedByteArrayResource extends ByteArrayResource {

        private final String filename;

        private NamedByteArrayResource(byte[] byteArray, String filename) {
            super(byteArray);
            this.filename = filename == null || filename.isBlank() ? "comprobante.jpg" : filename;
        }

        @Override
        public String getFilename() {
            return filename;
        }
    }
}
