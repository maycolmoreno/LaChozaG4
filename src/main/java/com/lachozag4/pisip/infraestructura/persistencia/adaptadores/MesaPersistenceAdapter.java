package com.lachozag4.pisip.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.lachozag4.pisip.aplicacion.puertos.salida.MesaRepositoryPort;
import com.lachozag4.pisip.dominio.entidades.Mesa;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.MesaJpaRepository;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.MesaMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MesaPersistenceAdapter implements MesaRepositoryPort {

    private final MesaJpaRepository mesaJpaRepository;

    @Override
    public List<Mesa> listarTodas() {
        return mesaJpaRepository.findAll()
                .stream()
                .map(MesaMapper::aDominio)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Mesa> buscarPorId(Long id) {
        return mesaJpaRepository.findById(id)
                .map(MesaMapper::aDominio);
    }

    @Override
    public Mesa guardar(Mesa mesa) {
        return MesaMapper.aDominio(
                mesaJpaRepository.save(
                        MesaMapper.aEntity(mesa)
                )
        );
    }
}
