package com.lachozag4.consumochoza.service;

import java.time.LocalDate;

import com.lachozag4.consumochoza.modelo.dto.ReporteVentasDiaDTO;

public interface IReporteService {

    ReporteVentasDiaDTO obtenerVentasDia(LocalDate fecha);
}

