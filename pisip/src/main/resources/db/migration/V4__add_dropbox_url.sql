-- V4: Soporte Dropbox para comprobantes de pago
-- Agrega columna url_dropbox (URL pública) y ruta_dropbox (path en Dropbox para gestión)
-- La columna ruta_relativa se mantiene por compatibilidad pero su semántica cambia:
-- en nuevos registros almacenará el path dentro de Dropbox (ej: /LaChoza/Comprobantes/archivo.jpg).

ALTER TABLE comprobante_pago
    ADD COLUMN IF NOT EXISTS url_dropbox    VARCHAR(1024),
    ADD COLUMN IF NOT EXISTS ruta_dropbox   VARCHAR(500);

-- Índice para búsquedas por estado de subida (url_dropbox IS NOT NULL = ya tiene Dropbox)
CREATE INDEX IF NOT EXISTS idx_comprobante_pago_url_dropbox
    ON comprobante_pago(url_dropbox)
    WHERE url_dropbox IS NOT NULL;

COMMENT ON COLUMN comprobante_pago.url_dropbox
    IS 'URL pública del comprobante en Dropbox (shared link). NULL si aún no se subió a Dropbox.';

COMMENT ON COLUMN comprobante_pago.ruta_dropbox
    IS 'Ruta del archivo dentro de Dropbox, ej: /LaChoza/Comprobantes/20250523_pago_8_abc123.jpg';
