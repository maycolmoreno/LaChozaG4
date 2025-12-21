package com.lachozag4.pisip.infraestructura.persistencia.mapeadores;

import com.lachozag4.pisip.dominio.entidades.Pedido;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.PedidoEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PedidoMapper {

    public Pedido aDominio(PedidoEntity entity) {
        if (entity == null) return null;

        Pedido dominio = new Pedido();
        dominio.setId(entity.getId());
        dominio.setFecha(entity.getFecha());
        dominio.setTotal(entity.getTotal());
        dominio.setIva(entity.getIva());
        dominio.setEstado(entity.getEstado());

        dominio.setCliente(ClienteMapper.aDominio(entity.getCliente()));
        dominio.setMesa(MesaMapper.aDominio(entity.getMesa()));

        if (entity.getDetalles() != null) {
            dominio.setDetalles(
                entity.getDetalles().stream()
                    .map(PedidoDetalleMapper::aDominio)
                    .collect(Collectors.toList())
            );
        }

        return dominio;
    }

    public PedidoEntity aEntity(Pedido dominio) {
        if (dominio == null) return null;

        PedidoEntity entity = new PedidoEntity();
        entity.setId(dominio.getId());
        entity.setFecha(dominio.getFecha());
        entity.setTotal(dominio.getTotal());
        entity.setIva(dominio.getIva());
        entity.setEstado(dominio.getEstado());

        entity.setCliente(ClienteMapper.aEntity(dominio.getCliente()));
        entity.setMesa(MesaMapper.aEntity(dominio.getMesa()));

        if (dominio.getDetalles() != null) {
            entity.setDetalles(
                dominio.getDetalles().stream()
                    .map(det -> {
                        var ent = PedidoDetalleMapper.aEntity(det);
                        ent.setPedido(entity); // relación bidireccional
                        return ent;
                    })
                    .collect(Collectors.toList())
            );
        }

        return entity;
    }
}
