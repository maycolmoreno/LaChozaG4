package com.lachozag4.pisip.aplicacion.puertos.salida;

import com.lachozag4.pisip.dominio.entidades.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoRepositoryPort {

    Optional<Producto> buscarPorId(Long id);

    List<Producto> listarTodos();

    Producto guardar(Producto producto); // 👈 ESTE ES EL IMPORTANTE
}
