-- V2: Tabla de comprobantes de pago (imágenes de transferencias)
CREATE TABLE IF NOT EXISTS comprobante_pago (
    idcomprobante    SERIAL PRIMARY KEY,
    "fkPago"         INTEGER        NOT NULL REFERENCES pago(idpago) ON DELETE CASCADE,
    nombre_archivo   VARCHAR(255)   NOT NULL,
    ruta_relativa    VARCHAR(500)   NOT NULL,
    content_type     VARCHAR(100)   NOT NULL,
    tamano           BIGINT         NOT NULL,
    usuario_registro VARCHAR(100)   NOT NULL,
    fecha_subida     TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_comprobante_pago_fk
    ON comprobante_pago("fkPago");
