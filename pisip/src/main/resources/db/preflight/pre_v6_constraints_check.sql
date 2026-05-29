-- =============================================================
-- Preflight V6 - Verificacion antes de indices unicos parciales
-- =============================================================
-- Uso recomendado:
--   psql "$DATABASE_URL" -f src/main/resources/db/preflight/pre_v6_constraints_check.sql
--
-- Este script NO modifica datos. Solo reporta registros que impedirian
-- aplicar V6__constraints_caja_cuenta_indexes.sql en una base existente.

-- 1) Debe existir como maximo una caja ABIERTA.
SELECT
    'CAJA_ABIERTA_DUPLICADA' AS problema,
    estado,
    COUNT(*) AS cantidad,
    STRING_AGG(idcaja::TEXT, ', ' ORDER BY idcaja) AS ids_caja
FROM caja_turno
WHERE estado = 'ABIERTA'
GROUP BY estado
HAVING COUNT(*) > 1;

-- 2) Cada mesa debe tener como maximo una cuenta ABIERTA.
SELECT
    'CUENTA_ABIERTA_DUPLICADA_POR_MESA' AS problema,
    fk_mesa,
    COUNT(*) AS cantidad,
    STRING_AGG(idcuenta::TEXT, ', ' ORDER BY idcuenta) AS ids_cuenta
FROM cuenta
WHERE estado = 'ABIERTA'
  AND fk_mesa IS NOT NULL
GROUP BY fk_mesa
HAVING COUNT(*) > 1;

-- 3) Pedidos con cuenta asignada a una mesa distinta.
SELECT
    'PEDIDO_CUENTA_MESA_DISTINTA' AS problema,
    p.idpedido,
    p.fk_mesa AS mesa_pedido,
    c.idcuenta,
    c.fk_mesa AS mesa_cuenta,
    p.estado AS estado_pedido,
    c.estado AS estado_cuenta
FROM pedido p
JOIN cuenta c ON c.idcuenta = p.fk_cuenta
WHERE p.fk_mesa IS NOT NULL
  AND c.fk_mesa IS NOT NULL
  AND p.fk_mesa <> c.fk_mesa
ORDER BY p.idpedido;

-- 4) Resumen rapido: si todos los valores son 0, V6 no deberia bloquearse
-- por datos duplicados conocidos.
SELECT
    (SELECT GREATEST(COUNT(*) - 1, 0) FROM caja_turno WHERE estado = 'ABIERTA') AS cajas_abiertas_sobrantes,
    (
        SELECT COUNT(*)
        FROM (
            SELECT fk_mesa
            FROM cuenta
            WHERE estado = 'ABIERTA'
              AND fk_mesa IS NOT NULL
            GROUP BY fk_mesa
            HAVING COUNT(*) > 1
        ) duplicadas
    ) AS mesas_con_cuentas_abiertas_duplicadas,
    (
        SELECT COUNT(*)
        FROM pedido p
        JOIN cuenta c ON c.idcuenta = p.fk_cuenta
        WHERE p.fk_mesa IS NOT NULL
          AND c.fk_mesa IS NOT NULL
          AND p.fk_mesa <> c.fk_mesa
    ) AS pedidos_con_cuenta_de_otra_mesa;
