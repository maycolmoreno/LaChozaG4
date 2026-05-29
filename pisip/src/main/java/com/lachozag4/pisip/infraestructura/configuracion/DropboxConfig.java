package com.lachozag4.pisip.infraestructura.configuracion;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración Spring para el cliente de Dropbox.
 * <p>
 * Propiedades requeridas en {@code application.properties}:
 * <pre>
 *   dropbox.app-key=tu_app_key
 *   dropbox.app-secret=tu_app_secret
 *   dropbox.refresh-token=tu_refresh_token
 *   dropbox.folder-root=/LaChoza/Comprobantes
 * </pre>
 *
 * <p><strong>Nota de seguridad:</strong> Las credenciales deben inyectarse
 * mediante variables de entorno, nunca hardcodeadas en el código fuente ni en repositorios.
 */
@Configuration
public class DropboxConfig {

     @Value("${dropbox.app-key}")
     private String appKey;

     @Value("${dropbox.app-secret}")
     private String appSecret;

     @Value("${dropbox.refresh-token}")
     private String refreshToken;

    /**
     * Crea el cliente Dropbox v2 como bean singleton de Spring.
     * Se utiliza un identificador de aplicación descriptivo para trazabilidad
     * en los logs de la API de Dropbox.
     */
    @Bean
public DbxClientV2 dbxClientV2() {
    validarConfiguracion();

    DbxRequestConfig config = DbxRequestConfig
            .newBuilder("LaChoza-POS/1.0")
            .build();

    // Pasar un string vacío en lugar de null para el access token
    DbxCredential credential = new DbxCredential(
            "",                    // accessToken — vacío, se renovará con refresh
            -1L,                   // expiresAt — forzar renovación inmediata
            refreshToken.trim(),
            appKey.trim(),
            appSecret.trim()
    );

    DbxClientV2 client = new DbxClientV2(config, credential);

    try {
        client.refreshAccessToken();
    } catch (Exception ex) {
        throw new IllegalStateException(
                "No se pudo autenticar con Dropbox. Verifica DROPBOX_APP_KEY, DROPBOX_APP_SECRET y DROPBOX_REFRESH_TOKEN.", ex);
    }

    return client;
}
    private void validarConfiguracion() {
        if (appKey == null || appKey.isBlank()) {
            throw new IllegalStateException("Falta configurar la variable de entorno DROPBOX_APP_KEY.");
        }
        if (appSecret == null || appSecret.isBlank()) {
            throw new IllegalStateException("Falta configurar la variable de entorno DROPBOX_APP_SECRET.");
        }
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalStateException("Falta configurar la variable de entorno DROPBOX_REFRESH_TOKEN.");
        }
    }
}
