CREATE TABLE IF NOT EXISTS pedido_historial (
    idhistorial      SERIAL       PRIMARY KEY,
    fk_pedido        INTEGER      NOT NULL REFERENCES pedido(idpedido),
    accion           VARCHAR(120) NOT NULL,
    estado_anterior  VARCHAR(30),
    estado_nuevo     VARCHAR(30),
    fk_usuario       INTEGER      REFERENCES usuario(idusuario),
    usuario_nombre   VARCHAR(150) NOT NULL,
    usuario_rol      VARCHAR(50)  NOT NULL,
    fecha            TIMESTAMP    NOT NULL,
    observacion      VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_pedido_historial_pedido_fecha
    ON pedido_historial (fk_pedido, fecha, idhistorial);

CREATE INDEX IF NOT EXISTS idx_pedido_historial_usuario
    ON pedido_historial (fk_usuario);
