CREATE UNIQUE INDEX IF NOT EXISTS uq_caja_turno_abierta
    ON caja_turno ((estado))
    WHERE estado = 'ABIERTA';

CREATE UNIQUE INDEX IF NOT EXISTS uq_cuenta_abierta_mesa
    ON cuenta (fk_mesa)
    WHERE estado = 'ABIERTA' AND fk_mesa IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_pedido_estado ON pedido (estado);
CREATE INDEX IF NOT EXISTS idx_pedido_mesa_estado ON pedido (fk_mesa, estado);
CREATE INDEX IF NOT EXISTS idx_pedido_cuenta ON pedido (fk_cuenta);
CREATE INDEX IF NOT EXISTS idx_cuenta_estado ON cuenta (estado);
CREATE INDEX IF NOT EXISTS idx_pago_cuenta ON pago (fk_cuenta);
CREATE INDEX IF NOT EXISTS idx_pago_caja_turno ON pago (fk_caja_turno);
