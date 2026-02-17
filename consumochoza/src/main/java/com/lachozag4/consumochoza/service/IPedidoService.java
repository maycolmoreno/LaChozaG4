package com.lachozag4.consumochoza.service;

import java.time.LocalDate;
import java.util.List;

import com.lachozag4.consumochoza.modelo.dto.PedidoDTO;
import com.lachozag4.consumochoza.modelo.dto.PedidosPaginadosDTO;

public interface IPedidoService {
    
    List<PedidoDTO> listarTodos();
    
    /**
     * Lista pedidos con filtros, ordenamiento y paginaciÃ³n.
     * 
     * @param estado Filtro por estado (PENDIENTE, EN_COCINA, LISTO_PARA_ENTREGA, COMPLETADO, CANCELADO) o null/TODOS
     * @param busqueda TÃ©rmino de bÃºsqueda (busca en ID, cliente, mesa, usuario, observaciones)
     * @param fechaDesde Fecha mÃ­nima (inclusive)
     * @param fechaHasta Fecha mÃ¡xima (inclusive)
     * @param page NÃºmero de pÃ¡gina (0-indexed)
     * @param size TamaÃ±o de pÃ¡gina
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

