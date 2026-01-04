package com.lachozag4.pisip.presentacion.mapeadores;

import com.lachozag4.pisip.presentacion.dto.PedidoRequestDTO;
import com.lachozag4.pisip.dominio.entidades.Pedido;
import com.lachozag4.pisip.dominio.entidades.PedidoDetalle;
import com.lachozag4.pisip.dominio.entidades.Producto;

import com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.MesaEntity;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.ClienteEntity;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.ProductoEntity;

import com.lachozag4.pisip.infraestructura.persistencia.jpa.MesaJpaRepository;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.ClienteJpaRepository;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.ProductoJpaRepository;

import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.MesaMapper;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.ClienteMapper;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.ProductoMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PedidoDTOMapper {

    // ✅ Ahora sí inyectamos los repos JPA correctos
    private final MesaJpaRepository mesaRepo;
    private final ClienteJpaRepository clienteRepo;
    private final ProductoJpaRepository productoRepo;

    public Pedido requestToEntity(PedidoRequestDTO dto) {

        Pedido pedido = new Pedido();
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado("PENDIENTE");

        // 1️⃣ Buscar MESA en JPA
        MesaEntity mesaEntity = mesaRepo.findById(dto.getMesaId())
                .orElseThrow(() ->
                        new RuntimeException("Mesa no encontrada con ID: " + dto.getMesaId())
                );
        pedido.setMesa(MesaMapper.aDominio(mesaEntity));

        // 2️⃣ Buscar CLIENTE en JPA
        ClienteEntity clienteEntity = clienteRepo.findById(dto.getClienteId())
                .orElseThrow(() ->
                        new RuntimeException("Cliente no encontrado con ID: " + dto.getClienteId())
                );
        pedido.setCliente(ClienteMapper.aDominio(clienteEntity));

        // 3️⃣ Mapear ITEMS → PedidoDetalle
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            pedido.setDetalles(
                dto.getItems().stream().map(item -> {

                    ProductoEntity productoEntity = productoRepo.findById(item.getProductoId())
                            .orElseThrow(() ->
                                    new RuntimeException("Producto no encontrado con ID: " + item.getProductoId())
                            );

                    Producto producto = ProductoMapper.aDominio(productoEntity);

                    PedidoDetalle detalle = new PedidoDetalle();
                    detalle.setProducto(producto);
                    detalle.setCantidad(item.getCantidad());
                    detalle.setPrecioUnitario(producto.getPrecio());

                    return detalle;
                }).collect(Collectors.toList())
            );
        }

        // 4️⃣ Calcular totales
        pedido.calcularTotales(15.0);

        return pedido;
    }
}
