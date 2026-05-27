package com.lachozag4.pisip.infraestructura.configuracion;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración Spring para el cliente de Dropbox.
 * <p>
 * Propiedades requeridas en {@code application.properties}:
 * <pre>
 *   dropbox.access-token=tu_token_de_acceso
 *   dropbox.folder-root=/LaChoza/Comprobantes
 * </pre>
 *
 * <p><strong>Nota de seguridad:</strong> En producción, el token debe inyectarse
 * mediante variable de entorno {@code DROPBOX_ACCESS_TOKEN}, nunca hardcodeado
 * en el código fuente ni en repositorios.
 */
@Configuration
public class DropboxConfig {

    @Value("${dropbox.access-token}")
    private String accessToken;

    /**
     * Crea el cliente Dropbox v2 como bean singleton de Spring.
     * Se utiliza un identificador de aplicación descriptivo para trazabilidad
     * en los logs de la API de Dropbox.
     */
    @Bean
    public DbxClientV2 dbxClientV2() {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalStateException("Falta configurar la variable de entorno DROPBOX_ACCESS_TOKEN para habilitar Dropbox.");
        }

        DbxRequestConfig config = DbxRequestConfig
                .newBuilder("LaChoza-POS/1.0")
                .build();
        return new DbxClientV2(config, accessToken.trim());
    }
}
