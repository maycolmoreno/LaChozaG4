package com.lachozag4.pisip.presentacion.mapeadores;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IClienteUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.IMesaUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.IProductoUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.IUsuarioUseCase;
import com.lachozag4.pisip.dominio.entidades.Cliente;
import com.lachozag4.pisip.dominio.entidades.Mesa;
import com.lachozag4.pisip.dominio.entidades.Producto;
import com.lachozag4.pisip.dominio.entidades.Usuario;
import com.lachozag4.pisip.presentacion.dto.request.PedidoDetalleRequestDTO;
import com.lachozag4.pisip.presentacion.dto.request.PedidoRequestDTO;

@ExtendWith(MockitoExtension.class)
class PedidoRequestMapperTest {

    @Mock private IUsuarioUseCase usuarioUseCase;
    @Mock private IMesaUseCase mesaUseCase;
    @Mock private IClienteUseCase clienteUseCase;
    @Mock private IProductoUseCase productoUseCase;

    @InjectMocks private PedidoRequestMapper mapper;

    @Test
    void usaPrecioDelProductoYNoPrecioEnviadoPorCliente() {
        when(usuarioUseCase.obtenerPorId(1))
                .thenReturn(new Usuario(1, "mesero", "", "Mesero", "CAMARERO", true, false));
        when(mesaUseCase.obtenerPorId(2))
                .thenReturn(new Mesa(2, 5, 4, true, null));
        when(clienteUseCase.obtenerPorId(3))
                .thenReturn(new Cliente(3, "Cliente", "000", null, null, null, true));
        when(productoUseCase.buscarPorId(4))
                .thenReturn(new Producto(4, "Seco de pollo", 7.50, 20, "", null, true, null));

        var detalle = new PedidoDetalleRequestDTO();
        detalle.setIdProducto(4);
        detalle.setCantidad(2);
        detalle.setPrecioUnitario(0.01);

        var request = new PedidoRequestDTO();
        request.setFecha(LocalDateTime.now());
        request.setIdUsuario(1);
        request.setIdMesa(2);
        request.setIdCliente(3);
        request.setDetalles(List.of(detalle));

        var pedido = mapper.toDomain(request);

        assertThat(pedido.getDetalles()).hasSize(1);
        assertThat(pedido.getDetalles().get(0).getPrecioUnitario()).isEqualTo(7.50);
    }
}
