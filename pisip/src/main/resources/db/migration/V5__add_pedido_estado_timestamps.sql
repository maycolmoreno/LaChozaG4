ALTER TABLE pedido
    ADD COLUMN IF NOT EXISTS fecha_en_cocina TIMESTAMP,
    ADD COLUMN IF NOT EXISTS fecha_listo_para_entrega TIMESTAMP,
    ADD COLUMN IF NOT EXISTS fecha_entregado TIMESTAMP;

UPDATE pedido
SET fecha_en_cocina = COALESCE(fecha_en_cocina, fecha)
WHERE estado IN ('EN_COCINA', 'EN_BAR', 'LISTO_PARA_ENTREGA', 'COMPLETADO')
  AND fecha_en_cocina IS NULL;

UPDATE pedido
SET fecha_listo_para_entrega = COALESCE(fecha_listo_para_entrega, fecha_en_cocina, fecha)
WHERE estado IN ('LISTO_PARA_ENTREGA', 'COMPLETADO')
  AND fecha_listo_para_entrega IS NULL;

UPDATE pedido
SET fecha_entregado = COALESCE(fecha_entregado, fecha_listo_para_entrega, fecha_en_cocina, fecha)
WHERE estado = 'COMPLETADO'
  AND fecha_entregado IS NULL;