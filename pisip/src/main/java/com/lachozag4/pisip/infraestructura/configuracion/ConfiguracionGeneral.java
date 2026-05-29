package com.lachozag4.pisip.infraestructura.configuracion;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.ICajaUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.ICategoriaUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.IClienteUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.IComedorUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.ICuentaUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.IMesaUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.IPagoUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.IPedidoDetalleUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.IPedidoUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.IProductoUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.IReporteUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.IUsuarioUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.impl.CajaUseCaseImpl;
import com.lachozag4.pisip.aplicacion.casosuso.impl.CategoriaUseCaseImpl;
import com.lachozag4.pisip.aplicacion.casosuso.impl.ClienteUseCaseImpl;
import com.lachozag4.pisip.aplicacion.casosuso.impl.ComedorUseCaseImpl;
import com.lachozag4.pisip.aplicacion.casosuso.impl.CuentaUseCaseImpl;
import com.lachozag4.pisip.aplicacion.casosuso.impl.MesaUseCaseImpl;
import com.lachozag4.pisip.aplicacion.casosuso.impl.PagoUseCaseImpl;
import com.lachozag4.pisip.aplicacion.casosuso.impl.PedidoDetalleUseCaseImpl;
import com.lachozag4.pisip.aplicacion.casosuso.impl.PedidoUseCaseImpl;
import com.lachozag4.pisip.aplicacion.casosuso.impl.ProductoUseCaseImpl;
import com.lachozag4.pisip.aplicacion.casosuso.impl.ReporteUseCaseImpl;
import com.lachozag4.pisip.aplicacion.casosuso.impl.UsuarioUseCaseImpl;
import com.lachozag4.pisip.aplicacion.servicios.GestionStockServicioImpl;
import com.lachozag4.pisip.dominio.entidades.Categoria;
import com.lachozag4.pisip.dominio.repositorios.ICajaTurnoRepositorio;
import com.lachozag4.pisip.dominio.repositorios.ICategoriaRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IClienteRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IComedorRepositorio;
import com.lachozag4.pisip.dominio.repositorios.ICuentaRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IMesaRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IPagoRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IPedidoDetalleRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IPedidoRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IProductoRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IUsuarioRepositorio;
import com.lachozag4.pisip.dominio.servicios.IGestionStockServicio;
import com.lachozag4.pisip.infraestructura.persistencia.adaptadores.CajaTurnoRepositorioImpl;
import com.lachozag4.pisip.infraestructura.persistencia.adaptadores.CategoriaRepositorioImpl;
import com.lachozag4.pisip.infraestructura.persistencia.adaptadores.ClienteRepositorioImpl;
import com.lachozag4.pisip.infraestructura.persistencia.adaptadores.ComedorRepositorioImpl;
import com.lachozag4.pisip.infraestructura.persistencia.adaptadores.CuentaRepositorioImpl;
import com.lachozag4.pisip.infraestructura.persistencia.adaptadores.MesaRepositorioImpl;
import com.lachozag4.pisip.infraestructura.persistencia.adaptadores.PagoRepositorioImpl;
import com.lachozag4.pisip.infraestructura.persistencia.adaptadores.PedidoDetalleRepositorioImpl;
import com.lachozag4.pisip.infraestructura.persistencia.adaptadores.PedidoRepositorioImpl;
import com.lachozag4.pisip.infraestructura.persistencia.adaptadores.ProductoRepositorioImpl;
import com.lachozag4.pisip.infraestructura.persistencia.adaptadores.UsuarioRepositorioImpl;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.ICategoriaJpaMapper;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.IClienteJpaMapper;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.IComedorJpaMapper;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.ICuentaJpaMapper;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.IMesaJpaMapper;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.IPedidoDetalleJpaMapper;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.IPedidoJpaMapper;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.IProductoJpaMapper;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.IUsuarioJpaMapper;
import com.lachozag4.pisip.infraestructura.repositorios.ICajaTurnoJpaRepositorio;
import com.lachozag4.pisip.infraestructura.repositorios.ICategoriaJpaRepositorio;
import com.lachozag4.pisip.infraestructura.repositorios.IClienteJpaRepositorio;
import com.lachozag4.pisip.infraestructura.repositorios.IComedorJpaRepositorio;
import com.lachozag4.pisip.infraestructura.repositorios.ICuentaJpaRepositorio;
import com.lachozag4.pisip.infraestructura.repositorios.IMesaJpaRepositorio;
import com.lachozag4.pisip.infraestructura.repositorios.IPagoJpaRepositorio;
import com.lachozag4.pisip.infraestructura.repositorios.IPedidoDetalleJpaRepositorio;
import com.lachozag4.pisip.infraestructura.repositorios.IPedidoJpaRepositorio;
import com.lachozag4.pisip.infraestructura.repositorios.IProductoJpaRepositorio;
import com.lachozag4.pisip.infraestructura.repositorios.IUsuarioJpaRepositorio;

@Configuration
public class ConfiguracionGeneral {

@Bean
ICategoriaRepositorio categoriaRepositorio(ICategoriaJpaRepositorio jpaRepository, ICategoriaJpaMapper mapper) {
return new CategoriaRepositorioImpl(jpaRepository, mapper);
}

@Bean
ICategoriaUseCase categoriaUseCase(ICategoriaRepositorio repositorio) {
return new CategoriaUseCaseImpl(repositorio);
}

@Bean
IProductoRepositorio productoRepositorio(IProductoJpaRepositorio jpaRepository, IProductoJpaMapper mapper) {
return new ProductoRepositorioImpl(jpaRepository, mapper);
}

@Bean
IProductoUseCase productoUseCase(IProductoRepositorio repositorio, ICategoriaRepositorio categoriaRepositorio) {
return new ProductoUseCaseImpl(repositorio, categoriaRepositorio);
}

@Bean
IClienteRepositorio clienteRepositorio(IClienteJpaRepositorio jpaRepository, IClienteJpaMapper mapper) {
return new ClienteRepositorioImpl(jpaRepository, mapper);
}

@Bean
ICuentaRepositorio cuentaRepositorio(ICuentaJpaRepositorio jpaRepository, ICuentaJpaMapper mapper) {
return new CuentaRepositorioImpl(jpaRepository, mapper);
}

@Bean
IClienteUseCase clienteUseCase(IClienteRepositorio repositorio) {
return new ClienteUseCaseImpl(repositorio);
}

@Bean
ICuentaUseCase cuentaUseCase(ICuentaRepositorio repositorio, IPedidoRepositorio pedidoRepositorio, IClienteRepositorio clienteRepositorio, IMesaRepositorio mesaRepositorio) {
return new CuentaUseCaseImpl(repositorio, pedidoRepositorio, clienteRepositorio, mesaRepositorio);
}

@Bean
IMesaRepositorio mesaRepositorio(IMesaJpaRepositorio jpaRepository, IMesaJpaMapper mapper) {
return new MesaRepositorioImpl(jpaRepository, mapper);
}

@Bean
IMesaUseCase mesaUseCase(IMesaRepositorio repositorio) {
return new MesaUseCaseImpl(repositorio);
}

@Bean
IComedorRepositorio comedorRepositorio(IComedorJpaRepositorio jpaRepository, IComedorJpaMapper mapper) {
return new ComedorRepositorioImpl(jpaRepository, mapper);
}

@Bean
IComedorUseCase comedorUseCase(IComedorRepositorio repositorio) {
return new ComedorUseCaseImpl(repositorio);
}

@Bean
IPedidoRepositorio pedidoRepositorio(IPedidoJpaRepositorio jpaRepository, IPedidoJpaMapper mapper) {
return new PedidoRepositorioImpl(jpaRepository, mapper);
}

@Bean
IGestionStockServicio gestionStockServicio(IProductoRepositorio productoRepositorio) {
return new GestionStockServicioImpl(productoRepositorio);
}

@Bean
IPedidoUseCase pedidoUseCase(IPedidoRepositorio repositorio, IGestionStockServicio stockServicio,
ICuentaRepositorio cuentaRepositorio,
IMesaRepositorio mesaRepositorio) {
return new PedidoUseCaseImpl(repositorio, stockServicio, cuentaRepositorio, mesaRepositorio);
}

@Bean
IPedidoDetalleRepositorio pedidodetalleRepositorio(IPedidoDetalleJpaRepositorio jpaRepository,
IPedidoDetalleJpaMapper mapper) {
return new PedidoDetalleRepositorioImpl(jpaRepository, mapper);
}

@Bean
IPedidoDetalleUseCase pedidodetalleUseCase(IPedidoDetalleRepositorio repositorio,
IPedidoRepositorio pedidoRepositorio, IProductoRepositorio productoRepositorio,
ICuentaRepositorio cuentaRepositorio, IGestionStockServicio stockServicio) {
return new PedidoDetalleUseCaseImpl(repositorio, pedidoRepositorio, productoRepositorio, cuentaRepositorio,
stockServicio);
}

@Bean
IUsuarioRepositorio usuarioRepositorio(IUsuarioJpaRepositorio jpaRepository, IUsuarioJpaMapper mapper) {
return new UsuarioRepositorioImpl(jpaRepository, mapper);
}

@Bean
IUsuarioUseCase usuarioUseCase(IUsuarioRepositorio repositorio, PasswordEncoder passwordEncoder) {
return new UsuarioUseCaseImpl(repositorio, passwordEncoder);
}

@Bean
ICajaTurnoRepositorio cajaTurnoRepositorio(ICajaTurnoJpaRepositorio jpaRepositorio) {
return new CajaTurnoRepositorioImpl(jpaRepositorio);
}

@Bean
IPagoRepositorio pagoRepositorio(IPagoJpaRepositorio jpaRepositorio) {
return new PagoRepositorioImpl(jpaRepositorio);
}

@Bean
ICajaUseCase cajaUseCase(ICajaTurnoRepositorio cajaRepositorio, IPagoRepositorio pagoRepositorio) {
return new CajaUseCaseImpl(cajaRepositorio, pagoRepositorio);
}

@Bean
IPagoUseCase pagoUseCase(IPagoRepositorio pagoRepositorio, ICuentaRepositorio cuentaRepositorio,
ICajaTurnoRepositorio cajaRepositorio, IMesaRepositorio mesaRepositorio,
com.lachozag4.pisip.aplicacion.servicios.ComprobanteService comprobanteService) {
return new PagoUseCaseImpl(pagoRepositorio, cuentaRepositorio, cajaRepositorio, mesaRepositorio, comprobanteService);
}

@Bean
IReporteUseCase reporteUseCase(IPedidoRepositorio pedidoRepositorio, IPagoRepositorio pagoRepositorio) {
return new ReporteUseCaseImpl(pedidoRepositorio, pagoRepositorio);
}

@Bean
CommandLineRunner asegurarCategoriaBar(ICategoriaRepositorio categoriaRepositorio) {
return args -> {
boolean existeBar = categoriaRepositorio.listarTodas().stream()
.anyMatch(c -> c.getNombre() != null && c.getNombre().trim().equalsIgnoreCase("BAR"));
if (!existeBar) {
categoriaRepositorio.guardar(
new Categoria(0, "BAR", "Bebidas y snacks (sin preparación de cocina)", true));
}
};
}
}
