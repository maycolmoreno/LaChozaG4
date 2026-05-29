package com.lachozag4.pisip.aplicacion.servicios;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.sharing.CreateSharedLinkWithSettingsErrorException;
import com.dropbox.core.v2.sharing.ListSharedLinksResult;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import com.lachozag4.pisip.dominio.servicios.IDropboxService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

/**
 * Implementación de {@link IDropboxService} usando el SDK oficial de Dropbox (v7).
 * <p>
 * Flujo de subida:
 * <ol>
 *   <li>Valida extensión y tamaño del archivo</li>
 *   <li>Sube el archivo con {@code WriteMode.ADD} (no sobreescribe)</li>
 *   <li>Crea un shared link público y devuelve la URL directa para visualización</li>
 * </ol>
 *
 * <p><strong>URL pública:</strong> Dropbox genera URLs del tipo
 * {@code https://www.dropbox.com/s/xxx/file.jpg?dl=0}. Esta implementación
 * las transforma a {@code https://dl.dropboxusercontent.com/s/xxx/file.jpg}
 * para que puedan ser usadas directamente en {@code <Image>} de MAUI.
 */
@Service
public class DropboxServiceImpl implements IDropboxService {

    private static final Logger log = LoggerFactory.getLogger(DropboxServiceImpl.class);

    private static final Set<String> EXTENSIONES_PERMITIDAS = Set.of("jpg", "jpeg", "png");
    private static final long        TAMANO_MAX_BYTES        = 5L * 1024 * 1024; // 5 MB

    private final DbxClientV2 dropboxClient;
    private final String      folderRoot;

    public DropboxServiceImpl(
            DbxClientV2 dropboxClient,
            @Value("${dropbox.folder-root:/LaChoza/Comprobantes}") String folderRoot) {
        this.dropboxClient = dropboxClient;
        // Asegura que el path empieze con / y no termine con /
        this.folderRoot = folderRoot.startsWith("/") ? folderRoot : "/" + folderRoot;
    }

    // ─── Subida ──────────────────────────────────────────────────────────────────

    @Override
    public String subirArchivo(String nombre, InputStream inputStream, long tamano) {
        if (tamano > TAMANO_MAX_BYTES) {
            throw new DropboxException(
                    String.format("El archivo excede el límite de %d MB", TAMANO_MAX_BYTES / 1024 / 1024));
        }

        String rutaDropbox = folderRoot + "/" + nombre;

        try {
            FileMetadata metadata = dropboxClient.files()
                    .uploadBuilder(rutaDropbox)
                    .withMode(WriteMode.ADD)
                    .withAutorename(true)   // evita duplicados automáticamente
                    .uploadAndFinish(inputStream);

            log.info("[Dropbox] Archivo subido: {} ({} bytes)", metadata.getPathDisplay(), tamano);
            return metadata.getPathDisplay();

        } catch (DbxException | java.io.IOException e) {
            log.error("[Dropbox] Error al subir archivo {}: {}", nombre, e.getMessage(), e);
            throw new DropboxException("Error al subir comprobante a Dropbox: " + e.getMessage(), e);
        }
    }

    // ─── URL pública ─────────────────────────────────────────────────────────────

    @Override
    public String obtenerUrlPublica(String rutaDropbox) {
        try {
            SharedLinkMetadata link = dropboxClient.sharing()
                    .createSharedLinkWithSettings(rutaDropbox);
            return convertirAUrlDirecta(link.getUrl());

        } catch (CreateSharedLinkWithSettingsErrorException e) {
            // Si ya existe un shared link para este archivo, lo reutilizamos
            if (e.errorValue.isSharedLinkAlreadyExists()) {
                log.debug("[Dropbox] Shared link ya existe para {}, recuperando...", rutaDropbox);
                return recuperarSharedLinkExistente(rutaDropbox);
            }
            log.error("[Dropbox] Error al crear shared link para {}: {}", rutaDropbox, e.getMessage());
            throw new DropboxException("Error al obtener URL pública de Dropbox: " + e.getMessage(), e);

        } catch (DbxException e) {
            log.error("[Dropbox] Error de API al obtener URL para {}: {}", rutaDropbox, e.getMessage());
            throw new DropboxException("Error de Dropbox al obtener URL: " + e.getMessage(), e);
        }
    }

    private String recuperarSharedLinkExistente(String rutaDropbox) {
        try {
            ListSharedLinksResult result = dropboxClient.sharing()
                    .listSharedLinksBuilder()
                    .withPath(rutaDropbox)
                    .withDirectOnly(true)
                    .start();

            List<SharedLinkMetadata> links = result.getLinks();
            if (links.isEmpty()) {
                throw new DropboxException("No se encontró shared link existente para: " + rutaDropbox);
            }
            return convertirAUrlDirecta(links.get(0).getUrl());

        } catch (DbxException e) {
            throw new DropboxException("Error al recuperar shared link de Dropbox: " + e.getMessage(), e);
        }
    }

    /**
     * Transforma la URL de Dropbox para descarga directa (visualización en MAUI).
     * <p>
     * {@code https://www.dropbox.com/s/xxx/file.jpg?dl=0}
     * → {@code https://dl.dropboxusercontent.com/s/xxx/file.jpg}
     */
    private String convertirAUrlDirecta(String dropboxUrl) {
        return dropboxUrl
                .replace("www.dropbox.com", "dl.dropboxusercontent.com")
                .replace("?dl=0", "")
                .replace("?dl=1", "");
    }

    // ─── Eliminación ─────────────────────────────────────────────────────────────

    @Override
    public void eliminarArchivo(String rutaDropbox) {
        try {
            dropboxClient.files().deleteV2(rutaDropbox);
            log.info("[Dropbox] Archivo eliminado: {}", rutaDropbox);
        } catch (DbxException e) {
            log.error("[Dropbox] Error al eliminar archivo {}: {}", rutaDropbox, e.getMessage());
            throw new DropboxException("Error al eliminar comprobante de Dropbox: " + e.getMessage(), e);
        }
    }

    @Override
    public void validarConexion() {
        try {
            dropboxClient.users().getCurrentAccount();
        } catch (DbxException e) {
            log.error("[Dropbox] Conexion no disponible: {}", e.getMessage());
            throw new DropboxException(
                    "Dropbox rechazo el token configurado o no permitio la autenticacion. Revise que sea un access token vigente de la app correcta.", e);
        }
    }

    // ─── Validación ──────────────────────────────────────────────────────────────

    @Override
    public void validarExtension(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.isBlank()) {
            throw new IllegalArgumentException("El nombre del archivo no puede estar vacío");
        }
        int puntoIdx = nombreArchivo.lastIndexOf('.');
        if (puntoIdx == -1 || puntoIdx == nombreArchivo.length() - 1) {
            throw new IllegalArgumentException("El archivo debe tener una extensión válida");
        }
        String ext = nombreArchivo.substring(puntoIdx + 1).toLowerCase();
        if (!EXTENSIONES_PERMITIDAS.contains(ext)) {
            throw new IllegalArgumentException(
                    "Extensión no permitida: ." + ext + ". Use: jpg, jpeg o png");
        }
    }
}
