package com.lachozag4.pisip.infraestructura.persistencia;

import com.lachozag4.pisip.aplicacion.puertos.salida.MesaRepositoryPort;
import com.lachozag4.pisip.dominio.entidades.Mesa;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.MesaJpaRepository;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.MesaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MesaPersistencia implements MesaRepositoryPort {

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
