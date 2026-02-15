package com.choza.consumochoza.service;

import java.time.LocalDate;
import java.util.List;

import com.choza.consumochoza.modelo.dto.PedidoDTO;
import com.choza.consumochoza.modelo.dto.PedidosPaginadosDTO;

public interface IPedidoService {
    
    List<PedidoDTO> listarTodos();
    
    /**
     * Lista pedidos con filtros, ordenamiento y paginación.
     * 
     * @param estado Filtro por estado (PENDIENTE, EN_COCINA, LISTO_PARA_ENTREGA, COMPLETADO, CANCELADO) o null/TODOS
     * @param busqueda Término de búsqueda (busca en ID, cliente, mesa, usuario, observaciones)
     * @param fechaDesde Fecha mínima (inclusive)
     * @param fechaHasta Fecha máxima (inclusive)
     * @param page Número de página (0-indexed)
     * @param size Tamaño de página
     * @return DTO con la lista paginada y metadatos
     */
    PedidosPaginadosDTO listarConFiltros(String estado, String busqueda, 
                                          LocalDate fechaDesde, LocalDate fechaHasta,
                                          int page, int size);
    
    PedidoDTO obtenerPorId(int id);
    
    PedidoDTO crear(PedidoDTO pedido);
    
    PedidoDTO actualizar(int id, PedidoDTO pedido);
    
    PedidoDTO cambiarEstado(int id, String estado);
    
    void eliminar(int id);
}
