package com.lachozag4.pisip.infraestructura.repositorios;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lachozag4.pisip.dominio.enums.EstadoPedido;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.PedidoJpa;

public interface IPedidoJpaRepositorio extends JpaRepository<PedidoJpa, Integer> {

	/**
	 * Obtiene un pedido con todas sus relaciones en un solo viaje a BD (evita N+1).
	 * Trae: usuario, mesa, cliente, detalles y productos.
	 */
	@Query("SELECT p FROM PedidoJpa p "
			+ "LEFT JOIN FETCH p.fkUsuario "
			+ "LEFT JOIN FETCH p.fkMesa "
			+ "LEFT JOIN FETCH p.fkCliente "
			+ "LEFT JOIN FETCH p.detalles d "
			+ "LEFT JOIN FETCH d.fkProducto "
			+ "WHERE p.idpedido = :id")
	Optional<PedidoJpa> findByIdWithRelations(@Param("id") int id);

	List<PedidoJpa> findByEstado(EstadoPedido estado);

	List<PedidoJpa> findByFkCuenta_Idcuenta(int idcuenta);

	/**
	 * Lista los pedidos activos (no finalizados) en una mesa específica.
	 * Retorna List para evitar IncorrectResultSizeDataAccessException cuando
	 * existen múltiples pedidos activos simultáneos en la misma mesa.
	 */
	@Query("SELECT p FROM PedidoJpa p WHERE p.fkMesa.idmesa = :idMesa AND p.estado NOT IN ('COMPLETADO', 'CANCELADO')")
	List<PedidoJpa> findPedidosActivosByMesa(@Param("idMesa") int idMesa);

	/**
	 * Verifica si existe algún pedido activo en la mesa, excluyendo el pedido indicado.
	 * Evita cargar entidades: retorna boolean directamente desde BD.
	 */
	@Query("""
		SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
		FROM PedidoJpa p
		WHERE p.fkMesa.idmesa = :idMesa
		  AND p.idpedido <> :excluirId
		  AND p.estado NOT IN ('COMPLETADO', 'CANCELADO')
		""")
	boolean existePedidoActivoPorMesaExcluyendo(@Param("idMesa") int idMesa, @Param("excluirId") int excluirId);

	/**
	 * Devuelve todos los pedidos COMPLETADOS cuya fecha esté entre inicio (inclusive)
	 * y fin (exclusive). Se hace FETCH de todas las relaciones para evitar N+1 al
	 * recorrer los detalles y productos en el reporte.
	 */
	@Query("""
		SELECT DISTINCT p FROM PedidoJpa p
		LEFT JOIN FETCH p.fkUsuario
		LEFT JOIN FETCH p.fkMesa
		LEFT JOIN FETCH p.fkCliente
		LEFT JOIN FETCH p.detalles d
		LEFT JOIN FETCH d.fkProducto
		WHERE p.fecha >= :inicio AND p.fecha < :fin
		  AND p.estado = 'COMPLETADO'
		""")
	List<PedidoJpa> findCompletadosBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

	/**
	 * Búsqueda paginada con filtros opcionales: estado, texto libre, rango de fechas.
	 * Usa native query con CAST explícito para evitar el problema de PostgreSQL
	 * "could not determine data type of parameter" cuando los parámetros son null.
	 */
	@Query(value = "SELECT p.* FROM pedido p "
			+ "LEFT JOIN usuario u ON u.idusuario = p.fk_usuario "
			+ "LEFT JOIN mesa m ON m.idmesa = p.fk_mesa "
			+ "LEFT JOIN cliente c ON c.idcliente = p.fk_cliente "
			+ "WHERE (CAST(:estado AS varchar) IS NULL OR p.estado = :estado) "
			+ "AND (CAST(:fechaDesde AS timestamp) IS NULL OR p.fecha >= :fechaDesde) "
			+ "AND (CAST(:fechaHasta AS timestamp) IS NULL OR p.fecha <= :fechaHasta) "
			+ "AND (:q = '' OR "
			+ "     CAST(p.idpedido AS varchar) LIKE '%' || :q || '%' OR "
			+ "     LOWER(c.nombre) LIKE LOWER('%' || :q || '%') OR "
			+ "     CAST(m.numero AS varchar) LIKE '%' || :q || '%' OR "
			+ "     LOWER(u.nombre_completo) LIKE LOWER('%' || :q || '%') OR "
			+ "     LOWER(p.observaciones) LIKE LOWER('%' || :q || '%'))",
		countQuery = "SELECT COUNT(*) FROM pedido p "
			+ "LEFT JOIN usuario u ON u.idusuario = p.fk_usuario "
			+ "LEFT JOIN mesa m ON m.idmesa = p.fk_mesa "
			+ "LEFT JOIN cliente c ON c.idcliente = p.fk_cliente "
			+ "WHERE (CAST(:estado AS varchar) IS NULL OR p.estado = :estado) "
			+ "AND (CAST(:fechaDesde AS timestamp) IS NULL OR p.fecha >= :fechaDesde) "
			+ "AND (CAST(:fechaHasta AS timestamp) IS NULL OR p.fecha <= :fechaHasta) "
			+ "AND (:q = '' OR "
			+ "     CAST(p.idpedido AS varchar) LIKE '%' || :q || '%' OR "
			+ "     LOWER(c.nombre) LIKE LOWER('%' || :q || '%') OR "
			+ "     CAST(m.numero AS varchar) LIKE '%' || :q || '%' OR "
			+ "     LOWER(u.nombre_completo) LIKE LOWER('%' || :q || '%') OR "
			+ "     LOWER(p.observaciones) LIKE LOWER('%' || :q || '%'))",
		nativeQuery = true)
	Page<PedidoJpa> buscarPaginado(
			@Param("estado") String estado,
			@Param("q") String q,
			@Param("fechaDesde") LocalDateTime fechaDesde,
			@Param("fechaHasta") LocalDateTime fechaHasta,
			Pageable pageable);

	/**
	 * Lista todos los pedidos con JOIN FETCH de todas sus relaciones para evitar N+1.
	 * Usar en lugar de findAll() cuando se necesita acceder a detalles, mesa, usuario, etc.
	 */
	@Query("""
		SELECT DISTINCT p FROM PedidoJpa p
		LEFT JOIN FETCH p.detalles d
		LEFT JOIN FETCH d.fkProducto
		LEFT JOIN FETCH p.fkMesa
		LEFT JOIN FETCH p.fkUsuario
		LEFT JOIN FETCH p.fkCliente
		""")
	List<PedidoJpa> findAllWithRelations();
}