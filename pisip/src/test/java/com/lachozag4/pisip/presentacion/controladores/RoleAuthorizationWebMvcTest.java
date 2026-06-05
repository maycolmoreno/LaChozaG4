package com.lachozag4.pisip.presentacion.controladores;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.ICajaUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.ICategoriaUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.IPagoUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.IPedidoUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.IProductoUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.IUsuarioUseCase;
import com.lachozag4.pisip.aplicacion.excepciones.NotFoundException;
import com.lachozag4.pisip.aplicacion.servicios.ComprobanteService;
import com.lachozag4.pisip.aplicacion.servicios.NotificacionService;
import com.lachozag4.pisip.aplicacion.servicios.PedidoHistorialService;
import com.lachozag4.pisip.dominio.entidades.CajaTurno;
import com.lachozag4.pisip.dominio.entidades.Categoria;
import com.lachozag4.pisip.dominio.entidades.Pago;
import com.lachozag4.pisip.dominio.entidades.Pedido;
import com.lachozag4.pisip.dominio.entidades.Producto;
import com.lachozag4.pisip.dominio.entidades.Usuario;
import com.lachozag4.pisip.infraestructura.seguridad.JwtUtil;
import com.lachozag4.pisip.presentacion.dto.request.CategoriaRequestDTO;
import com.lachozag4.pisip.presentacion.dto.request.PedidoRequestDTO;
import com.lachozag4.pisip.presentacion.dto.request.ProductoRequestDTO;
import com.lachozag4.pisip.presentacion.dto.request.UsuarioRequestDTO;
import com.lachozag4.pisip.presentacion.dto.response.CategoriaResponseDTO;
import com.lachozag4.pisip.presentacion.dto.response.PedidoResponseDTO;
import com.lachozag4.pisip.presentacion.dto.response.ProductoResponseDTO;
import com.lachozag4.pisip.presentacion.dto.response.UsuarioResponseDTO;
import com.lachozag4.pisip.presentacion.mapeadores.ICategoriaDtoMapper;
import com.lachozag4.pisip.presentacion.mapeadores.IPedidoDtoMapper;
import com.lachozag4.pisip.presentacion.mapeadores.IProductoDtoMapper;
import com.lachozag4.pisip.presentacion.mapeadores.IUsuarioDtoMapper;
import com.lachozag4.pisip.presentacion.mapeadores.PedidoRequestMapper;
import com.lachozag4.pisip.presentacion.mapeadores.ProductoRequestMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        PedidoControlador.class,
        PagoControlador.class,
        CajaControlador.class,
        ProductoControlador.class,
        CategoriaControlador.class,
        UsuarioControlador.class
})
class RoleAuthorizationWebMvcTest {

    private static final String PEDIDO_VALIDO = """
            {
              "fecha": "2026-05-28T12:00:00",
              "idUsuario": 1,
              "idMesa": 1,
              "idCliente": 1,
              "detalles": [
                {
                  "idProducto": 1,
                  "cantidad": 1,
                  "precioUnitario": 1.0
                }
              ]
            }
            """;

    private static final String PAGO_VALIDO = """
            {
              "monto": 10.0,
              "metodo": "EFECTIVO",
              "usuario": "cajero"
            }
            """;

    private static final String APERTURA_CAJA_VALIDA = """
            {
              "montoInicial": 20.0,
              "usuarioApertura": "cajero"
            }
            """;

    private static final String CIERRE_CAJA_VALIDO = """
            {
              "montoDeclaradoCierre": 20.0,
              "usuarioCierre": "cajero"
            }
            """;

    private static final String PRODUCTO_VALIDO = """
            {
              "nombre": "Seco de pollo",
              "precio": 7.50,
              "stockActual": 10,
              "descripcion": "Con arroz",
              "imagenUrl": "",
              "estado": true,
              "categoriaId": 1
            }
            """;

    private static final String CATEGORIA_VALIDA = """
            {
              "idcategoria": 1,
              "nombre": "Platos",
              "descripcion": "Menu principal",
              "estado": true
            }
            """;

    private static final String USUARIO_VALIDO = """
            {
              "username": "admin2",
              "password": "secret",
              "nombreCompleto": "Administrador Dos",
              "rol": "ADMIN",
              "estado": true,
              "requiereCambioPassword": false
            }
            """;

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private IPedidoUseCase pedidoUseCase;

    @MockitoBean
    private IPedidoDtoMapper pedidoDtoMapper;

    @MockitoBean
    private PedidoRequestMapper pedidoRequestMapper;

    @MockitoBean
    private NotificacionService notificacionService;

    @MockitoBean
    private PedidoHistorialService pedidoHistorialService;

    @MockitoBean
    private IPagoUseCase pagoUseCase;

    @MockitoBean
    private ICajaUseCase cajaUseCase;

    @MockitoBean
    private IProductoUseCase productoUseCase;

    @MockitoBean
    private IProductoDtoMapper productoDtoMapper;

    @MockitoBean
    private ProductoRequestMapper productoRequestMapper;

    @MockitoBean
    private ICategoriaUseCase categoriaUseCase;

    @MockitoBean
    private ICategoriaDtoMapper categoriaDtoMapper;

    @MockitoBean
    private IUsuarioUseCase usuarioUseCase;

    @MockitoBean
    private IUsuarioDtoMapper usuarioDtoMapper;

    @MockitoBean
    private ComprobanteService comprobanteService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void cajeroPuedeCrearPedidos() throws Exception {
        Pedido pedido = pedidoDominio(Pedido.ESTADO_PENDIENTE);
        PedidoResponseDTO response = pedidoResponse(16, Pedido.ESTADO_PENDIENTE);

        when(pedidoRequestMapper.toDomain(any(PedidoRequestDTO.class))).thenReturn(pedido);
        when(pedidoUseCase.crear(pedido)).thenReturn(pedido);
        when(pedidoDtoMapper.toResponseDTO(pedido)).thenReturn(response);
        when(cajaUseCase.obtenerCajaAbierta()).thenReturn(cajaAbierta());

        mvc.perform(post("/api/pedidos")
                        .with(user("cajero").roles("CAJERO"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PEDIDO_VALIDO))
                .andExpect(status().isCreated());

        verify(pedidoUseCase).crear(pedido);
    }

    @Test
    void cajeroNoPuedeCrearPedidoSinCajaAbierta() throws Exception {
        when(cajaUseCase.obtenerCajaAbierta())
                .thenThrow(new NotFoundException("No hay una caja abierta actualmente"));

        mvc.perform(post("/api/pedidos")
                        .with(user("cajero").roles("CAJERO"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PEDIDO_VALIDO))
                .andExpect(status().isBadRequest());

        verify(cajaUseCase).obtenerCajaAbierta();
        verifyNoInteractions(pedidoRequestMapper);
        verifyNoInteractions(pedidoUseCase);
    }

    @Test
    void cajeroNoPuedeMarcarPedidoListo() throws Exception {
        mvc.perform(patch("/api/pedidos/10/listo")
                        .with(user("cajero").roles("CAJERO")))
                .andExpect(status().isForbidden());

        verifyNoInteractions(pedidoUseCase);
    }

    @Test
    void cajeroPuedeEnviarPedidoACocina() throws Exception {
        Pedido pedido = pedidoDominio(Pedido.ESTADO_EN_COCINA);
        when(pedidoUseCase.cambiarEstado(10, Pedido.ESTADO_EN_COCINA)).thenReturn(pedido);
        when(pedidoDtoMapper.toResponseDTO(pedido)).thenReturn(pedidoResponse(10, Pedido.ESTADO_EN_COCINA));
        when(cajaUseCase.obtenerCajaAbierta()).thenReturn(cajaAbierta());

        mvc.perform(patch("/api/pedidos/10/confirmar")
                        .with(user("cajero").roles("CAJERO")))
                .andExpect(status().isOk());

        verify(pedidoUseCase).cambiarEstado(10, Pedido.ESTADO_EN_COCINA);
    }

    @Test
    void cocinaNoPuedeCobrarCuenta() throws Exception {
        mvc.perform(post("/api/cuentas/7/pagos")
                        .with(user("cocina").roles("COCINA"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PAGO_VALIDO))
                .andExpect(status().isForbidden());

        verifyNoInteractions(pagoUseCase);
    }

    @Test
    void camareroNoPuedeCancelarPedidos() throws Exception {
        mvc.perform(patch("/api/pedidos/10/cancelar")
                        .with(user("camarero").roles("CAMARERO")))
                .andExpect(status().isForbidden());

        verifyNoInteractions(pedidoUseCase);
    }

    @Test
    void cajeroNoPuedeEliminarPedidosPorDelete() throws Exception {
        mvc.perform(delete("/api/pedidos/10")
                        .with(user("cajero").roles("CAJERO")))
                .andExpect(status().isForbidden());

        verifyNoInteractions(pedidoUseCase);
    }

    @Test
    void camareroPuedeCrearPedidos() throws Exception {
        Pedido pedido = pedidoDominio(Pedido.ESTADO_PENDIENTE);
        PedidoResponseDTO response = pedidoResponse(15, Pedido.ESTADO_PENDIENTE);

        when(pedidoRequestMapper.toDomain(any(PedidoRequestDTO.class))).thenReturn(pedido);
        when(pedidoUseCase.crear(pedido)).thenReturn(pedido);
        when(pedidoDtoMapper.toResponseDTO(pedido)).thenReturn(response);

        mvc.perform(post("/api/pedidos")
                        .with(user("camarero").roles("CAMARERO"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PEDIDO_VALIDO))
                .andExpect(status().isCreated());

        verify(pedidoUseCase).crear(pedido);
        verify(cajaUseCase, never()).obtenerCajaAbierta();
    }

    @Test
    void camareroPuedeEntregarPedido() throws Exception {
        Pedido pedido = pedidoDominio(Pedido.ESTADO_COMPLETADO);
        when(pedidoUseCase.cambiarEstado(10, Pedido.ESTADO_COMPLETADO)).thenReturn(pedido);
        when(pedidoDtoMapper.toResponseDTO(pedido)).thenReturn(pedidoResponse(10, Pedido.ESTADO_COMPLETADO));

        mvc.perform(patch("/api/pedidos/10/entregado")
                        .with(user("camarero").roles("CAMARERO")))
                .andExpect(status().isOk());

        verify(pedidoUseCase).cambiarEstado(10, Pedido.ESTADO_COMPLETADO);
    }

    @Test
    void cocinaPuedeMarcarPedidoListo() throws Exception {
        Pedido pedido = pedidoDominio(Pedido.ESTADO_LISTO_PARA_ENTREGA);
        when(pedidoUseCase.cambiarEstado(10, Pedido.ESTADO_LISTO_PARA_ENTREGA)).thenReturn(pedido);
        when(pedidoDtoMapper.toResponseDTO(pedido)).thenReturn(pedidoResponse(10, Pedido.ESTADO_LISTO_PARA_ENTREGA));

        mvc.perform(patch("/api/pedidos/10/listo")
                        .with(user("cocina").roles("COCINA")))
                .andExpect(status().isOk());

        verify(pedidoUseCase).cambiarEstado(10, Pedido.ESTADO_LISTO_PARA_ENTREGA);
    }

    @Test
    void adminPuedeCancelarPedido() throws Exception {
        Pedido pedido = pedidoDominio(Pedido.ESTADO_CANCELADO);
        when(pedidoUseCase.cambiarEstado(10, Pedido.ESTADO_CANCELADO)).thenReturn(pedido);
        when(pedidoDtoMapper.toResponseDTO(pedido)).thenReturn(pedidoResponse(10, Pedido.ESTADO_CANCELADO));

        mvc.perform(patch("/api/pedidos/10/cancelar")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());

        verify(pedidoUseCase).cambiarEstado(10, Pedido.ESTADO_CANCELADO);
    }

    @Test
    void adminPuedeEliminarPedidoPorDelete() throws Exception {
        mvc.perform(delete("/api/pedidos/10")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());

        verify(pedidoUseCase).eliminar(10);
    }

    @Test
    void cajeroPuedeRegistrarPago() throws Exception {
        Pago pago = new Pago(30, LocalDateTime.now(), 10.0, Pago.METODO_EFECTIVO, null, "cajero", 7, 3);

        when(pagoUseCase.registrarPago(eq(7), eq(10.0), eq(Pago.METODO_EFECTIVO), isNull(), eq("cajero")))
                .thenReturn(pago);
        when(pagoUseCase.totalPagadoCuenta(7)).thenReturn(10.0);
        when(pagoUseCase.saldoPendienteCuenta(7)).thenReturn(0.0);

        mvc.perform(post("/api/cuentas/7/pagos")
                        .with(user("cajero").roles("CAJERO"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PAGO_VALIDO))
                .andExpect(status().isCreated());

        verify(pagoUseCase).registrarPago(7, 10.0, Pago.METODO_EFECTIVO, null, "cajero");
    }

    @Test
    void cajeroPuedeAbrirCaja() throws Exception {
        CajaTurno caja = new CajaTurno(3, LocalDateTime.now(), null, 20.0, null, null, null,
                CajaTurno.ESTADO_ABIERTA, "cajero", null, null);

        when(cajaUseCase.abrirCaja(eq(20.0), eq("cajero"), isNull())).thenReturn(caja);

        mvc.perform(post("/api/caja/apertura")
                        .with(user("cajero").roles("CAJERO"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(APERTURA_CAJA_VALIDA))
                .andExpect(status().isCreated());

        verify(cajaUseCase).abrirCaja(20.0, "cajero", null);
    }

    @Test
    void camareroNoPuedeCrearProductos() throws Exception {
        mvc.perform(post("/api/productos")
                        .with(user("camarero").roles("CAMARERO"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PRODUCTO_VALIDO))
                .andExpect(status().isForbidden());

        verifyNoInteractions(productoUseCase);
    }

    @Test
    void cajeroNoPuedeCrearCategorias() throws Exception {
        mvc.perform(post("/api/categorias")
                        .with(user("cajero").roles("CAJERO"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CATEGORIA_VALIDA))
                .andExpect(status().isForbidden());

        verifyNoInteractions(categoriaUseCase);
    }

    @Test
    void cocinaNoPuedeCrearUsuarios() throws Exception {
        mvc.perform(post("/api/usuarios")
                        .with(user("cocina").roles("COCINA"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USUARIO_VALIDO))
                .andExpect(status().isForbidden());

        verifyNoInteractions(usuarioUseCase);
    }

    @Test
    void cocinaNoPuedeAbrirCaja() throws Exception {
        mvc.perform(post("/api/caja/apertura")
                        .with(user("cocina").roles("COCINA"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(APERTURA_CAJA_VALIDA))
                .andExpect(status().isForbidden());

        verifyNoInteractions(cajaUseCase);
    }

    @Test
    void cocinaNoPuedeCerrarCaja() throws Exception {
        mvc.perform(post("/api/caja/cierre")
                        .with(user("cocina").roles("COCINA"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CIERRE_CAJA_VALIDO))
                .andExpect(status().isForbidden());

        verifyNoInteractions(cajaUseCase);
    }

    @Test
    void cajeroNoPuedeActualizarUsuarios() throws Exception {
        mvc.perform(put("/api/usuarios/9")
                        .with(user("cajero").roles("CAJERO"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USUARIO_VALIDO))
                .andExpect(status().isForbidden());

        verifyNoInteractions(usuarioUseCase);
    }

    @Test
    void camareroNoPuedeActualizarCategorias() throws Exception {
        mvc.perform(put("/api/categorias/1")
                        .with(user("camarero").roles("CAMARERO"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CATEGORIA_VALIDA))
                .andExpect(status().isForbidden());

        verifyNoInteractions(categoriaUseCase);
    }

    @Test
    void camareroNoPuedeEliminarProductos() throws Exception {
        mvc.perform(delete("/api/productos/4")
                        .with(user("camarero").roles("CAMARERO")))
                .andExpect(status().isForbidden());

        verifyNoInteractions(productoUseCase);
    }

    @Test
    void adminPuedeCrearProducto() throws Exception {
        Categoria categoria = new Categoria(1, "Platos", "Menu principal", true);
        Producto producto = new Producto(4, "Seco de pollo", 7.50, 10, "Con arroz", "", null, true, categoria);
        ProductoResponseDTO response = new ProductoResponseDTO();
        response.setIdproducto(4);
        response.setNombre("Seco de pollo");
        response.setPrecio(7.50);
        response.setStockActual(10);
        response.setEstado(true);
        response.setCategoriaId(1);

        when(productoRequestMapper.toDomain(any(ProductoRequestDTO.class))).thenReturn(producto);
        when(productoRequestMapper.getCategoriaId(any(ProductoRequestDTO.class))).thenReturn(1);
        when(productoUseCase.crear(producto, 1)).thenReturn(producto);
        when(productoDtoMapper.toResponseDTO(producto)).thenReturn(response);

        mvc.perform(post("/api/productos")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PRODUCTO_VALIDO))
                .andExpect(status().isCreated());

        verify(productoUseCase).crear(producto, 1);
    }

    @Test
    void adminPuedeCrearCategoria() throws Exception {
        Categoria categoria = new Categoria(1, "Platos", "Menu principal", true);
        CategoriaResponseDTO response = new CategoriaResponseDTO();
        response.setIdcategoria(1);
        response.setNombre("Platos");
        response.setDescripcion("Menu principal");
        response.setEstado(true);

        when(categoriaDtoMapper.toDomain(any(CategoriaRequestDTO.class))).thenReturn(categoria);
        when(categoriaUseCase.crear(categoria)).thenReturn(categoria);
        when(categoriaDtoMapper.toResponseDTO(categoria)).thenReturn(response);

        mvc.perform(post("/api/categorias")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CATEGORIA_VALIDA))
                .andExpect(status().isCreated());

        verify(categoriaUseCase).crear(categoria);
    }

    @Test
    void adminPuedeCrearUsuario() throws Exception {
        Usuario usuario = new Usuario(9, "admin2", "secret", "Administrador Dos", "ADMIN", true, false);
        UsuarioResponseDTO response = new UsuarioResponseDTO();
        response.setIdusuario(9);
        response.setUsername("admin2");
        response.setNombreCompleto("Administrador Dos");
        response.setRol("ADMIN");
        response.setEstado(true);

        when(usuarioDtoMapper.toDomain(any(UsuarioRequestDTO.class))).thenReturn(usuario);
        when(usuarioUseCase.crear(usuario)).thenReturn(usuario);
        when(usuarioDtoMapper.toResponseDTO(usuario)).thenReturn(response);

        mvc.perform(post("/api/usuarios")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USUARIO_VALIDO))
                .andExpect(status().isCreated());

        verify(usuarioUseCase).crear(usuario);
    }

    @Test
    void setupAdminSeBloqueaSiYaExistenUsuarios() throws Exception {
        Usuario existente = new Usuario(1, "romo", "hash", "Administrador", "ADMIN", true, false);
        when(usuarioUseCase.listar()).thenReturn(List.of(existente));

        mvc.perform(post("/api/usuarios/setup-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USUARIO_VALIDO))
                .andExpect(status().isForbidden());

        verify(usuarioUseCase, never()).crear(any(Usuario.class));
        verifyNoInteractions(usuarioDtoMapper);
    }

    @Test
    void setupAdminCreaAdminSoloCuandoNoHayUsuarios() throws Exception {
        Usuario usuario = new Usuario(9, "admin2", "secret", "Administrador Dos", "ADMIN", true, false);
        UsuarioResponseDTO response = new UsuarioResponseDTO();
        response.setIdusuario(9);
        response.setUsername("admin2");
        response.setNombreCompleto("Administrador Dos");
        response.setRol("ADMIN");
        response.setEstado(true);

        when(usuarioUseCase.listar()).thenReturn(List.of());
        when(usuarioDtoMapper.toDomain(any(UsuarioRequestDTO.class))).thenReturn(usuario);
        when(usuarioUseCase.crear(usuario)).thenReturn(usuario);
        when(usuarioDtoMapper.toResponseDTO(usuario)).thenReturn(response);

        mvc.perform(post("/api/usuarios/setup-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USUARIO_VALIDO))
                .andExpect(status().isCreated());

        verify(usuarioUseCase).crear(usuario);
    }

    @Test
    void adminPuedeActualizarProducto() throws Exception {
        Categoria categoria = new Categoria(1, "Platos", "Menu principal", true);
        Producto producto = new Producto(4, "Seco de pollo", 7.50, 10, "Con arroz", "", null, true, categoria);
        ProductoResponseDTO response = new ProductoResponseDTO();
        response.setIdproducto(4);
        response.setNombre("Seco de pollo");
        response.setPrecio(7.50);
        response.setStockActual(10);
        response.setEstado(true);
        response.setCategoriaId(1);

        when(productoRequestMapper.toDomain(any(ProductoRequestDTO.class))).thenReturn(producto);
        when(productoRequestMapper.getCategoriaId(any(ProductoRequestDTO.class))).thenReturn(1);
        when(productoUseCase.actualizar(4, producto, 1)).thenReturn(producto);
        when(productoDtoMapper.toResponseDTO(producto)).thenReturn(response);

        mvc.perform(put("/api/productos/4")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PRODUCTO_VALIDO))
                .andExpect(status().isOk());

        verify(productoUseCase).actualizar(4, producto, 1);
    }

    @Test
    void adminPuedeEliminarUsuario() throws Exception {
        mvc.perform(delete("/api/usuarios/9")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());

        verify(usuarioUseCase).eliminar(9);
    }

    private Pedido pedidoDominio(String estado) {
        return new Pedido(10, LocalDateTime.now(), null, null, null, estado, null,
                null, null, null, null, List.of());
    }

    private PedidoResponseDTO pedidoResponse(int id, String estado) {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setIdpedido(id);
        dto.setEstado(estado);
        dto.setFecha(LocalDateTime.now());
        return dto;
    }

    private CajaTurno cajaAbierta() {
        return new CajaTurno(3, LocalDateTime.now(), null, 20.0, null, null, null,
                CajaTurno.ESTADO_ABIERTA, "cajero", null, null);
    }

    @TestConfiguration
    @EnableMethodSecurity
    static class SecurityForTests {
        @Bean
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            return http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(HttpMethod.POST, "/api/usuarios/setup-admin").permitAll()
                            .anyRequest().authenticated())
                    .build();
        }
    }
}
