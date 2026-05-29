package com.choza.consumochoza.service.impl;

import com.choza.consumochoza.service.ApiClientException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

final class ApiErrorUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ApiErrorUtil() {
    }

    static ApiClientException toApiException(String defaultMessage, Throwable ex) {
        String mensaje = extractMessage(ex);
        if (mensaje == null || mensaje.isBlank()) {
            mensaje = defaultMessage;
        }
        return new ApiClientException(mensaje, ex);
    }

    static String extractMessage(Throwable ex) {
        if (ex instanceof org.springframework.web.reactive.function.client.WebClientResponseException webEx) {
            String body = webEx.getResponseBodyAsString();
            String parsed = extractMessage(body);
            if (parsed != null) {
                return parsed;
            }
        }
        return ex.getMessage();
    }

    static String extractMessage(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }

        try {
            JsonNode root = MAPPER.readTree(raw);
            for (String field : new String[] { "mensaje", "message", "error", "title", "detail" }) {
                JsonNode value = root.get(field);
                if (value != null && value.isTextual() && !value.asText().isBlank()) {
                    return value.asText();
                }
            }
        } catch (Exception ignored) {
        }

        return raw.trim();
    }
}
