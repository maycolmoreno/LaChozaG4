package com.choza.consumochoza.service;

import java.time.LocalDate;

import com.choza.consumochoza.modelo.dto.ReporteVentasDiaDTO;

public interface IReporteService {

    ReporteVentasDiaDTO obtenerVentasDia(LocalDate fecha);
}
