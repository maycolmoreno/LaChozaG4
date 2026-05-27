package com.lachozag4.pisip.dominio.servicios;

import java.io.InputStream;

/**
 * Contrato de dominio para el servicio de almacenamiento en Dropbox.
 * <p>
 * Permite desacoplar la lógica de negocio de la implementación concreta del SDK de Dropbox,
 * facilitando pruebas unitarias mediante mocks y sustitución futura de proveedor.
 */
public interface IDropboxService {

    /**
     * Sube un archivo al directorio configurado en Dropbox.
     *
     * @param nombre      Nombre único con el que se guardará el archivo en Dropbox
     *                    (ej: {@code 20250523_pago_8_abc123.jpg})
     * @param inputStream Stream del contenido del archivo
     * @param tamano      Tamaño del archivo en bytes (para metadata)
     * @return Ruta completa del archivo dentro de Dropbox
     *         (ej: {@code /LaChoza/Comprobantes/20250523_pago_8_abc123.jpg})
     * @throws DropboxException si ocurre un error de comunicación o autenticación
     */
    String subirArchivo(String nombre, InputStream inputStream, long tamano);

    /**
     * Obtiene la URL pública (shared link) de un archivo en Dropbox.
     * Si el shared link ya existe, lo retorna sin crear uno nuevo.
     *
     * @param rutaDropbox Ruta completa del archivo en Dropbox
     * @return URL directa para mostrar/descargar la imagen
     * @throws DropboxException si el archivo no existe o hay error de red
     */
    String obtenerUrlPublica(String rutaDropbox);

    /**
     * Elimina un archivo de Dropbox.
     *
     * @param rutaDropbox Ruta completa del archivo en Dropbox
     * @throws DropboxException si el archivo no existe o hay error de red
     */
    void eliminarArchivo(String rutaDropbox);

    /**
     * Valida que la extensión del archivo sea permitida (jpg, jpeg, png).
     *
     * @param nombreArchivo Nombre original del archivo incluyendo extensión
     * @throws IllegalArgumentException si la extensión no está permitida
     */
    void validarExtension(String nombreArchivo);

    /**
     * Excepción de dominio para errores de Dropbox.
     */
    class DropboxException extends RuntimeException {
        public DropboxException(String message) { super(message); }
        public DropboxException(String message, Throwable cause) { super(message, cause); }
    }
}
