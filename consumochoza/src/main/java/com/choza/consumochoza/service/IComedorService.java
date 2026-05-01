package com.choza.consumochoza.service;

import java.util.List;

import com.choza.consumochoza.modelo.dto.ComedorDTO;

public interface IComedorService {

    List<ComedorDTO> listarTodos();

    List<ComedorDTO> listarActivos();

    ComedorDTO obtenerPorId(int id);

    ComedorDTO crear(ComedorDTO comedor);

    ComedorDTO actualizar(int id, ComedorDTO comedor);

    void eliminar(int id);
}
