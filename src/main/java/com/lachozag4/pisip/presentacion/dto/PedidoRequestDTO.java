package com.lachozag4.pisip.presentacion.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class PedidoRequestDTO {
    private Long mesaId;
    private Long clienteId;
    private List<ItemDTO> items;

    @Getter
    @Setter
    public static class ItemDTO {
        private Long productoId;
        private Integer cantidad;
    }
}
