package com.lachozag4.pisip.aplicacion.puertos.salida;

import com.lachozag4.pisip.dominio.entidades.Mesa;
import java.util.List;
import java.util.Optional;

public interface MesaRepositoryPort {

    List<Mesa> listarTodas();

    Optional<Mesa> buscarPorId(Long id);

    Mesa guardar(Mesa mesa);
}
