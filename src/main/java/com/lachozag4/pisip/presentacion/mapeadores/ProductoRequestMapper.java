package com.lachozag4.pisip.presentacion.mapeadores;

import org.springframework.stereotype.Component;

import com.lachozag4.pisip.dominio.entidades.Producto;
import com.lachozag4.pisip.presentacion.dto.request.ProductoRequestDTO;

/**
 * Mapper que convierte ProductoRequestDTO (capa presentación) a Producto (capa dominio).
 * Esto permite que la capa de aplicación no conozca los DTOs de presentación,
 * respetando el principio de inversión de dependencias (DIP) y la arquitectura limpia.
 */
@Component
public class ProductoRequestMapper {

    /**
     * Convierte un ProductoRequestDTO en una entidad de dominio Producto.
     * La categoría se resuelve en el caso de uso, aquí solo se pasa el ID.
     * 
     * @param dto el DTO de request con los datos del producto
     * @return la entidad de dominio Producto (sin categoría asignada, se asigna en el caso de uso)
     */
    public Producto toDomain(ProductoRequestDTO dto) {
        return new Producto(
                dto.getIdproducto(),
                dto.getNombre(),
                dto.getPrecio(),
                dto.getStockActual(),
                dto.getDescripcion(),
                dto.getImagenUrl(),
                dto.isEstado(),
                null // La categoría se resuelve en el caso de uso
        );
    }

    /**
     * Obtiene el ID de categoría desde el DTO.
     * 
     * @param dto el DTO de request
     * @return el ID de la categoría
     */
    public int getCategoriaId(ProductoRequestDTO dto) {
        return dto.getCategoriaId();
    }
}
