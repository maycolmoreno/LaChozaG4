package com.lachozag4.pisip.aplicacion.servicios;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.lachozag4.pisip.dominio.entidades.Pedido;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.PedidoHistorialJpa;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.PedidoJpa;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.UsuarioJpa;
import com.lachozag4.pisip.infraestructura.repositorios.IPedidoHistorialJpaRepositorio;
import com.lachozag4.pisip.infraestructura.repositorios.IPedidoJpaRepositorio;
import com.lachozag4.pisip.infraestructura.repositorios.IUsuarioJpaRepositorio;
import com.lachozag4.pisip.presentacion.dto.response.PedidoHistorialResponseDTO;

@Service
public class PedidoHistorialService {

    public static final String ACCION_CREAR_PEDIDO = "Pedido creado";
    public static final String ACCION_GUARDAR_PEDIDO = "Pedido guardado";
    public static final String ACCION_ENVIAR_COCINA = "Enviado a cocina";
    public static final String ACCION_INICIAR_PREPARACION = "Preparacion iniciada";
    public static final String ACCION_MARCAR_LISTO = "Marcado como listo";
    public static final String ACCION_ENTREGAR_CLIENTE = "Entregado al cliente";
    public static final String ACCION_COBRAR_CUENTA = "Cuenta cobrada";
    public static final String ACCION_CERRAR_MESA = "Mesa cerrada";
    public static final String ACCION_CANCELAR_PEDIDO = "Pedido cancelado";

    private static final Logger log = LoggerFactory.getLogger(PedidoHistorialService.class);

    private final IPedidoHistorialJpaRepositorio historialRepositorio;
    private final IPedidoJpaRepositorio pedidoRepositorio;
    private final IUsuarioJpaRepositorio usuarioRepositorio;

    public PedidoHistorialService(
            IPedidoHistorialJpaRepositorio historialRepositorio,
            IPedidoJpaRepositorio pedidoRepositorio,
            IUsuarioJpaRepositorio usuarioRepositorio) {
        this.historialRepositorio = historialRepositorio;
        this.pedidoRepositorio = pedidoRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    public void registrarCreacion(Pedido pedido) {
        registrar(pedido.getIdpedido(), ACCION_CREAR_PEDIDO, null, pedido.getEstado(), null);
    }

    public void registrarGuardado(Pedido pedido) {
        registrar(pedido.getIdpedido(), ACCION_GUARDAR_PEDIDO, pedido.getEstado(), pedido.getEstado(), null);
    }

    public void registrarCambioEstado(Pedido anterior, Pedido actualizado) {
        registrar(
                actualizado.getIdpedido(),
                accionPorTransicion(anterior.getEstado(), actualizado.getEstado()),
                anterior.getEstado(),
                actualizado.getEstado(),
                null);
    }

    public void registrarEvento(int idpedido, String accion, String estadoAnterior, String estadoNuevo, String observacion) {
        registrar(idpedido, accion, estadoAnterior, estadoNuevo, observacion);
    }

    public List<PedidoHistorialResponseDTO> listarPorPedido(int idpedido) {
        return historialRepositorio.findByFkPedido_IdpedidoOrderByFechaAscIdhistorialAsc(idpedido)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private void registrar(int idpedido, String accion, String estadoAnterior, String estadoNuevo, String observacion) {
        try {
            PedidoJpa pedido = pedidoRepositorio.findById(idpedido)
                    .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado para historial: " + idpedido));
            UsuarioSnapshot usuario = resolverUsuarioActual();

            PedidoHistorialJpa historial = new PedidoHistorialJpa();
            historial.setFkPedido(pedido);
            historial.setFkUsuario(usuario.usuario());
            historial.setAccion(accion);
            historial.setEstadoAnterior(estadoAnterior);
            historial.setEstadoNuevo(estadoNuevo);
            historial.setUsuarioNombre(usuario.nombre());
            historial.setUsuarioRol(usuario.rol());
            historial.setFecha(LocalDateTime.now());
            historial.setObservacion(observacion);

            historialRepositorio.save(historial);
        } catch (Exception ex) {
            log.error("No se pudo registrar historial de pedido {} accion {}", idpedido, accion, ex);
        }
    }

    private UsuarioSnapshot resolverUsuarioActual() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            return new UsuarioSnapshot(null, "SISTEMA", "SISTEMA");
        }

        String username = authentication.getName();
        return usuarioRepositorio.findByUsername(username)
                .map(usuario -> new UsuarioSnapshot(usuario, usuario.getNombreCompleto(), usuario.getRol()))
                .orElseGet(() -> new UsuarioSnapshot(null, username, rolDesdeAuthorities()));
    }

    private String rolDesdeAuthorities() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return "SISTEMA";
        }
        return authentication.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .findFirst()
                .orElse("SISTEMA");
    }

    private String accionPorTransicion(String anterior, String nuevo) {
        if (Pedido.ESTADO_EN_COCINA.equals(nuevo)) {
            return esRolActual("COCINA") ? ACCION_INICIAR_PREPARACION : ACCION_ENVIAR_COCINA;
        }
        if (Pedido.ESTADO_LISTO_PARA_ENTREGA.equals(nuevo) || "LISTO".equals(nuevo)) {
            return ACCION_MARCAR_LISTO;
        }
        if (Pedido.ESTADO_COMPLETADO.equals(nuevo) || "ENTREGADO".equals(nuevo)) {
            return ACCION_ENTREGAR_CLIENTE;
        }
        if (Pedido.ESTADO_CANCELADO.equals(nuevo)) {
            return ACCION_CANCELAR_PEDIDO;
        }
        return "Cambio de estado";
    }

    private boolean esRolActual(String rol) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(a -> ("ROLE_" + rol).equals(a.getAuthority()));
    }

    private PedidoHistorialResponseDTO toDto(PedidoHistorialJpa historial) {
        UsuarioJpa usuario = historial.getFkUsuario();
        return new PedidoHistorialResponseDTO(
                historial.getIdhistorial(),
                historial.getFkPedido().getIdpedido(),
                historial.getAccion(),
                historial.getEstadoAnterior(),
                historial.getEstadoNuevo(),
                usuario == null ? null : usuario.getIdusuario(),
                historial.getUsuarioNombre(),
                historial.getUsuarioRol(),
                historial.getFecha(),
                historial.getObservacion());
    }

    private record UsuarioSnapshot(UsuarioJpa usuario, String nombre, String rol) {
    }
}
