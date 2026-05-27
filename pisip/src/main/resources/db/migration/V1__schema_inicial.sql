-- =============================================================
-- V1 — Schema inicial de La Choza
-- Generado desde las entidades JPA con SpringPhysicalNamingStrategy
-- (camelCase → snake_case en nombres de columna sin @Column(name=...))
--
-- NOTA: Este script solo se ejecuta en bases de datos NUEVAS y vacías.
-- Para BD existentes se usa baseline-on-migrate=true → Flyway marca
-- este script como aplicado sin ejecutarlo.
-- =============================================================

-- ----------------------------
-- Tablas independientes (sin FK)
-- ----------------------------

CREATE TABLE IF NOT EXISTS categoria (
    idcategoria        SERIAL       PRIMARY KEY,
    nombre             VARCHAR(100) NOT NULL UNIQUE,
    descripcion        VARCHAR(500),
    estado             BOOLEAN      NOT NULL
);

CREATE TABLE IF NOT EXISTS comedor (
    idcomedor          SERIAL       PRIMARY KEY,
    nombre             VARCHAR(100) NOT NULL UNIQUE,
    descripcion        VARCHAR(500),
    estado             BOOLEAN      NOT NULL
);

CREATE TABLE IF NOT EXISTS usuario (
    idusuario                  SERIAL       PRIMARY KEY,
    username                   VARCHAR(50)  NOT NULL UNIQUE,
    password                   VARCHAR(255) NOT NULL,
    nombre_completo            VARCHAR(150) NOT NULL,
    rol                        VARCHAR(50)  NOT NULL,
    estado                     BOOLEAN      NOT NULL,
    requiere_cambio_password   BOOLEAN      NOT NULL
);

CREATE TABLE IF NOT EXISTS cliente (
    idcliente    SERIAL       PRIMARY KEY,
    nombre       VARCHAR(150) NOT NULL,
    cedula       VARCHAR(20)  NOT NULL UNIQUE,
    telefono     VARCHAR(20),
    direccion    VARCHAR(255),
    email        VARCHAR(150) UNIQUE,
    estado       BOOLEAN      NOT NULL
);

CREATE TABLE IF NOT EXISTS caja_turno (
    idcaja                     SERIAL          PRIMARY KEY,
    fecha_apertura             TIMESTAMP       NOT NULL,
    fecha_cierre               TIMESTAMP,
    monto_inicial              DOUBLE PRECISION NOT NULL,
    monto_esperado_cierre      DOUBLE PRECISION,
    monto_declarado_cierre     DOUBLE PRECISION,
    diferencia                 DOUBLE PRECISION,
    estado                     VARCHAR(20)     NOT NULL,
    usuario_apertura           VARCHAR(100)    NOT NULL,
    usuario_cierre             VARCHAR(100),
    observaciones              VARCHAR(255)
);

-- ----------------------------
-- Tablas con FK de nivel 1
-- ----------------------------

CREATE TABLE IF NOT EXISTS mesa (
    idmesa       SERIAL   PRIMARY KEY,
    numero       INTEGER  NOT NULL UNIQUE,
    capacidad    INTEGER  NOT NULL,
    estado       BOOLEAN  NOT NULL,
    idcomedor    INTEGER  REFERENCES comedor(idcomedor)
);

CREATE TABLE IF NOT EXISTS producto (
    idproducto      SERIAL           PRIMARY KEY,
    nombre          VARCHAR(200)     NOT NULL,
    precio          DOUBLE PRECISION NOT NULL,
    stock_actual    INTEGER          NOT NULL,
    descripcion     VARCHAR(500),
    imagen_url      VARCHAR(500),
    estado          BOOLEAN          NOT NULL,
    fk_categoria_id INTEGER          NOT NULL REFERENCES categoria(idcategoria),
    CONSTRAINT uq_producto_nombre_categoria UNIQUE (nombre, fk_categoria_id)
);

-- ----------------------------
-- Tablas con FK de nivel 2
-- ----------------------------

CREATE TABLE IF NOT EXISTS cuenta (
    idcuenta        SERIAL           PRIMARY KEY,
    fecha_apertura  TIMESTAMP        NOT NULL,
    fecha_cierre    TIMESTAMP,
    estado          VARCHAR(20)      NOT NULL,
    total           DOUBLE PRECISION NOT NULL,
    fk_mesa         INTEGER          REFERENCES mesa(idmesa),
    fk_cliente      INTEGER          REFERENCES cliente(idcliente)
);

-- ----------------------------
-- Tablas con FK de nivel 3
-- ----------------------------

CREATE TABLE IF NOT EXISTS pedido (
    idpedido        SERIAL       PRIMARY KEY,
    fecha           TIMESTAMP    NOT NULL,
    estado          VARCHAR(30)  NOT NULL,
    observaciones   VARCHAR(255),
    fk_usuario      INTEGER      REFERENCES usuario(idusuario),
    fk_mesa         INTEGER      REFERENCES mesa(idmesa),
    fk_cliente      INTEGER      REFERENCES cliente(idcliente),
    fk_cuenta       INTEGER      REFERENCES cuenta(idcuenta)
);

CREATE TABLE IF NOT EXISTS pago (
    idpago          SERIAL           PRIMARY KEY,
    fecha           TIMESTAMP        NOT NULL,
    monto           DOUBLE PRECISION NOT NULL,
    metodo          VARCHAR(30)      NOT NULL,
    referencia      VARCHAR(120),
    usuario         VARCHAR(100)     NOT NULL,
    fk_cuenta       INTEGER          NOT NULL REFERENCES cuenta(idcuenta),
    fk_caja_turno   INTEGER          NOT NULL REFERENCES caja_turno(idcaja)
);

-- ----------------------------
-- Tablas con FK de nivel 4
-- ----------------------------

CREATE TABLE IF NOT EXISTS pedido_detalle (
    idpedidodetalle  SERIAL           PRIMARY KEY,
    fk_pedido        INTEGER          REFERENCES pedido(idpedido),
    fk_producto      INTEGER          REFERENCES producto(idproducto),
    cantidad         INTEGER          NOT NULL,
    precio_unitario  DOUBLE PRECISION NOT NULL
);
