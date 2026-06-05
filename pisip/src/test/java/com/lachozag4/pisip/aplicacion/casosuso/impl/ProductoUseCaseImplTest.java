package com.lachozag4.pisip.aplicacion.casosuso.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lachozag4.pisip.dominio.entidades.Categoria;
import com.lachozag4.pisip.dominio.entidades.Producto;
import com.lachozag4.pisip.dominio.repositorios.ICategoriaRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IProductoRepositorio;

@ExtendWith(MockitoExtension.class)
class ProductoUseCaseImplTest {

    @Mock private IProductoRepositorio productoRepositorio;
    @Mock private ICategoriaRepositorio categoriaRepositorio;

    @InjectMocks private ProductoUseCaseImpl useCase;

    @Test
    void eliminarDesactivaProductoSinBorrarloFisicamenteParaMantenerHistorial() {
        var categoria = new Categoria(2, "Platos", "Menu principal", true);
        var producto = new Producto(7, "Seco de pollo", 7.50, 12, "Con arroz", "foto.jpg", null, true, categoria);

        when(productoRepositorio.buscarPorId(7)).thenReturn(Optional.of(producto));
        when(productoRepositorio.guardar(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.eliminar(7);

        var captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepositorio).guardar(captor.capture());
        verify(productoRepositorio, never()).eliminar(7);

        Producto guardado = captor.getValue();
        assertThat(guardado.getIdproducto()).isEqualTo(7);
        assertThat(guardado.getNombre()).isEqualTo("Seco de pollo");
        assertThat(guardado.isEstado()).isFalse();
        assertThat(guardado.getFkCategoria()).isSameAs(categoria);
    }
}
