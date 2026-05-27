-- V3: Renombrar columna "fkPago" (case-sensitive) a fk_pago (snake_case)
-- para que coincida con la naming strategy de Hibernate (SpringPhysicalNamingStrategy)
ALTER TABLE comprobante_pago RENAME COLUMN "fkPago" TO fk_pago;

DROP INDEX IF EXISTS idx_comprobante_pago_fk;
CREATE INDEX idx_comprobante_pago_fk ON comprobante_pago(fk_pago);
