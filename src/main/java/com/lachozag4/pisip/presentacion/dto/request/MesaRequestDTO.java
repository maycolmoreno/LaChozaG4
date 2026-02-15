package com.lachozag4.pisip.presentacion.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class MesaRequestDTO {

    @Positive
    private int numero;

    @Positive
    private int capacidad;

    private boolean estado;

  
}
