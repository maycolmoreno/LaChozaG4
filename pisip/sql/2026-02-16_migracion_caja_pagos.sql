-- Migracion manual para produccion (PostgreSQL)
-- Modulo: Caja + Pagos
-- Fecha: 2026-02-16

BEGIN;

CREATE TABLE IF NOT EXISTS caja_turno (
    idcaja BIGSERIAL PRIMARY KEY,
    fecha_apertura TIMESTAMP NOT NULL,
    fecha_cierre TIMESTAMP NULL,
    monto_inicial DOUBLE PRECISION NOT NULL CHECK (monto_inicial >= 0),
    monto_esperado_cierre DOUBLE PRECISION NULL,
    monto_declarado_cierre DOUBLE PRECISION NULL CHECK (monto_declarado_cierre >= 0),
    diferencia DOUBLE PRECISION NULL,
    estado VARCHAR(20) NOT NULL,
    usuario_apertura VARCHAR(100) NOT NULL,
    usuario_cierre VARCHAR(100) NULL,
    observaciones VARCHAR(255) NULL
);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'ck_caja_turno_estado'
    ) THEN
        ALTER TABLE caja_turno
            ADD CONSTRAINT ck_caja_turno_estado
            CHECK (estado IN ('ABIERTA', 'CERRADA'));
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS pago (
    idpago BIGSERIAL PRIMARY KEY,
    fecha TIMESTAMP NOT NULL,
    monto DOUBLE PRECISION NOT NULL CHECK (monto > 0),
    metodo VARCHAR(30) NOT NULL,
    referencia VARCHAR(120) NULL,
    usuario VARCHAR(100) NOT NULL,
    fk_cuenta BIGINT NOT NULL,
    fk_caja_turno BIGINT NOT NULL,
    CONSTRAINT fk_pago_cuenta
        FOREIGN KEY (fk_cuenta) REFERENCES cuenta(idcuenta),
    CONSTRAINT fk_pago_caja_turno
        FOREIGN KEY (fk_caja_turno) REFERENCES caja_turno(idcaja)
);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'ck_pago_metodo'
    ) THEN
        ALTER TABLE pago
            ADD CONSTRAINT ck_pago_metodo
            CHECK (metodo IN ('EFECTIVO', 'TARJETA', 'TRANSFERENCIA', 'OTRO'));
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_caja_turno_estado ON caja_turno(estado);
CREATE INDEX IF NOT EXISTS idx_pago_fk_cuenta ON pago(fk_cuenta);
CREATE INDEX IF NOT EXISTS idx_pago_fk_caja_turno ON pago(fk_caja_turno);
CREATE INDEX IF NOT EXISTS idx_pago_fecha ON pago(fecha);

COMMIT;
