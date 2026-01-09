// ============================================
// VARIABLES GLOBALES
// ============================================
let pedidosData = [];
let pedidosFiltrados = [];
let mesasData = [];
let productosData = [];
let carrito = [];

// ============================================
// INICIALIZACIÓN
// ============================================
document.addEventListener("DOMContentLoaded", () => {
    console.log("Iniciando módulo de pedidos...");
    
    // Solo ejecutar si estamos en la página de pedidos
    if (document.getElementById("contenedor-pedidos")) {
        cargarMesas();
        cargarProductos();
        cargarPedidos();
        inicializarFiltros();
        
        // Auto-actualizar cada 30 segundos
        setInterval(cargarPedidos, 30000);
    }
});

// ============================================
// CARGAR MESAS
// ============================================
async function cargarMesas() {
    try {
        const response = await fetch("/api/mesas");
        
        if (!response.ok) {
            throw new Error("Error al cargar mesas");
        }
        
        mesasData = await response.json();
        console.log("Mesas cargadas:", mesasData.length);
        
        // Actualizar select de filtro
        const selectFiltro = document.getElementById("filtroMesa");
        if (selectFiltro) {
            selectFiltro.innerHTML = '<option value="">Todas las mesas</option>';
            mesasData.forEach(mesa => {
                const option = document.createElement("option");
                option.value = mesa.numero;
                option.textContent = `Mesa ${mesa.numero}`;
                selectFiltro.appendChild(option);
            });
        }
        
    } catch (error) {
        console.error("Error cargando mesas:", error);
    }
}

// ============================================
// CARGAR PRODUCTOS
// ============================================
async function cargarProductos() {
    try {
        const response = await fetch("/api/productos");
        
        if (!response.ok) {
            throw new Error("Error al cargar productos");
        }
        
        productosData = await response.json();
        console.log("Productos cargados:", productosData.length);
        
    } catch (error) {
        console.error("Error cargando productos:", error);
    }
}

// ============================================
// ABRIR MODAL NUEVO PEDIDO
// ============================================
function abrirModalNuevoPedido() {
    // Limpiar carrito
    carrito = [];
    actualizarVistaCarrito();
    
    // Cargar mesas en el select
    const selectMesa = document.getElementById("nuevoPedidoMesa");
    selectMesa.innerHTML = '<option value="">-- Seleccione una mesa --</option>';
    mesasData.forEach(mesa => {
        const option = document.createElement("option");
        option.value = mesa.id;
        option.textContent = `Mesa ${mesa.numero} - ${mesa.ubicacion || ''} (${mesa.estado})`;
        option.disabled = mesa.estado !== 'LIBRE';
        selectMesa.appendChild(option);
    });
    
    // Mostrar productos
    mostrarProductosParaPedido(productosData);
    
    // Inicializar búsqueda
    document.getElementById("buscarProducto").addEventListener("input", (e) => {
        const termino = e.target.value.toLowerCase();
        const productosFiltrados = productosData.filter(p => 
            p.nombre.toLowerCase().includes(termino)
        );
        mostrarProductosParaPedido(productosFiltrados);
    });
    
    // Abrir modal
    const modal = new bootstrap.Modal(document.getElementById("modal-nuevo-pedido"));
    modal.show();
}

// ============================================
// MOSTRAR PRODUCTOS PARA PEDIDO
// ============================================
function mostrarProductosParaPedido(productos) {
    const contenedor = document.getElementById("listaProductos");
    
    if (productos.length === 0) {
        contenedor.innerHTML = '<div class="col-12 text-center text-muted">No hay productos disponibles</div>';
        return;
    }
    
    contenedor.innerHTML = productos.map(producto => {
        const disponible = producto.disponible && producto.stockActual > 0;
        const imagenUrl = producto.imagenUrl || "https://via.placeholder.com/150x150/4CAF50/FFFFFF?text=Sin+Imagen";
        
        return `
            <div class="col-md-3 mb-3">
                <div class="card ${!disponible ? 'opacity-50' : ''}" style="cursor: pointer;">
                    <img src="${imagenUrl}" class="card-img-top" style="height: 100px; object-fit: cover;">
                    <div class="card-body p-2">
                        <h6 class="card-title mb-1" style="font-size: 0.9rem;">${producto.nombre}</h6>
                        <p class="mb-1"><strong>${parseFloat(producto.precio).toFixed(2)}</strong></p>
                        <p class="mb-2 small text-muted">Stock: ${producto.stockActual}</p>
                        <button class="btn btn-sm btn-success w-100" 
                                onclick="agregarAlCarrito(${producto.id})"
                                ${!disponible ? 'disabled' : ''}>
                            ➕ Agregar
                        </button>
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

// ============================================
// AGREGAR AL CARRITO
// ============================================
function agregarAlCarrito(productoId) {
    const producto = productosData.find(p => p.id === productoId);
    
    if (!producto) return;
    
    // Verificar si ya está en el carrito
    const itemExistente = carrito.find(item => item.productoId === productoId);
    
    if (itemExistente) {
        // Verificar stock
        if (itemExistente.cantidad >= producto.stockActual) {
            mostrarNotificacion("⚠️ No hay más stock disponible", "warning");
            return;
        }
        itemExistente.cantidad++;
    } else {
        carrito.push({
            productoId: producto.id,
            nombre: producto.nombre,
            precio: producto.precio,
            cantidad: 1,
            stockMax: producto.stockActual
        });
    }
    
    actualizarVistaCarrito();
    mostrarNotificacion(`✅ ${producto.nombre} agregado`, "success");
}

// ============================================
// ACTUALIZAR VISTA CARRITO
// ============================================
function actualizarVistaCarrito() {
    const contenedor = document.getElementById("carritoItems");
    
    if (carrito.length === 0) {
        contenedor.innerHTML = '<p class="text-muted text-center py-3">No hay items agregados</p>';
        document.getElementById("carritoSubtotal").textContent = "$0.00";
        document.getElementById("carritoIva").textContent = "$0.00";
        document.getElementById("carritoTotal").textContent = "$0.00";
        return;
    }
    
    let html = '<table class="table table-sm">';
    html += '<thead><tr><th>Producto</th><th width="150">Cantidad</th><th width="100" class="text-end">Subtotal</th><th width="50"></th></tr></thead>';
    html += '<tbody>';
    
    carrito.forEach((item, index) => {
        const subtotal = item.precio * item.cantidad;
        html += `
            <tr>
                <td>${item.nombre}</td>
                <td>
                    <div class="input-group input-group-sm">
                        <button class="btn btn-outline-secondary" onclick="cambiarCantidad(${index}, -1)">-</button>
                        <input type="number" class="form-control text-center" value="${item.cantidad}" 
                               min="1" max="${item.stockMax}" 
                               onchange="actualizarCantidad(${index}, this.value)">
                        <button class="btn btn-outline-secondary" onclick="cambiarCantidad(${index}, 1)">+</button>
                    </div>
                </td>
                <td class="text-end">${subtotal.toFixed(2)}</td>
                <td>
                    <button class="btn btn-sm btn-danger" onclick="eliminarDelCarrito(${index})">🗑️</button>
                </td>
            </tr>
        `;
    });
    
    html += '</tbody></table>';
    contenedor.innerHTML = html;
    
    // Calcular totales
    const subtotal = carrito.reduce((sum, item) => sum + (item.precio * item.cantidad), 0);
    const iva = subtotal * 0.15;
    const total = subtotal + iva;
    
    document.getElementById("carritoSubtotal").textContent = `${subtotal.toFixed(2)}`;
    document.getElementById("carritoIva").textContent = `${iva.toFixed(2)}`;
    document.getElementById("carritoTotal").textContent = `${total.toFixed(2)}`;
}

// ============================================
// CAMBIAR CANTIDAD
// ============================================
function cambiarCantidad(index, cambio) {
    const item = carrito[index];
    const nuevaCantidad = item.cantidad + cambio;
    
    if (nuevaCantidad < 1) {
        eliminarDelCarrito(index);
        return;
    }
    
    if (nuevaCantidad > item.stockMax) {
        mostrarNotificacion("⚠️ No hay más stock disponible", "warning");
        return;
    }
    
    item.cantidad = nuevaCantidad;
    actualizarVistaCarrito();
}

function actualizarCantidad(index, nuevaCantidad) {
    nuevaCantidad = parseInt(nuevaCantidad);
    
    if (isNaN(nuevaCantidad) || nuevaCantidad < 1) {
        carrito[index].cantidad = 1;
    } else if (nuevaCantidad > carrito[index].stockMax) {
        carrito[index].cantidad = carrito[index].stockMax;
        mostrarNotificacion("⚠️ Cantidad ajustada al stock disponible", "warning");
    } else {
        carrito[index].cantidad = nuevaCantidad;
    }
    
    actualizarVistaCarrito();
}

// ============================================
// ELIMINAR DEL CARRITO
// ============================================
function eliminarDelCarrito(index) {
    carrito.splice(index, 1);
    actualizarVistaCarrito();
}

// ============================================
// CONFIRMAR NUEVO PEDIDO
// ============================================
async function confirmarNuevoPedido() {
    const mesaId = document.getElementById("nuevoPedidoMesa").value;
    
    // Validaciones
    if (!mesaId) {
        mostrarNotificacion("⚠️ Debe seleccionar una mesa", "warning");
        return;
    }
    
    if (carrito.length === 0) {
        mostrarNotificacion("⚠️ Debe agregar al menos un producto", "warning");
        return;
    }
    
    // Preparar datos del pedido
    const pedidoData = {
        mesaId: parseInt(mesaId),
        clienteId: null, // Opcional por ahora
        items: carrito.map(item => ({
            productoId: item.productoId,
            cantidad: item.cantidad
        }))
    };
    
    try {
        const response = await fetch("/api/pedidos", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(pedidoData)
        });
        
        if (response.ok) {
            mostrarNotificacion("✅ Pedido creado exitosamente", "success");
            
            // Cerrar modal
            const modal = bootstrap.Modal.getInstance(document.getElementById("modal-nuevo-pedido"));
            modal.hide();
            
            // Recargar pedidos
            setTimeout(() => cargarPedidos(), 500);
        } else {
            const error = await response.text();
            mostrarNotificacion(`❌ Error: ${error}`, "danger");
        }
    } catch (error) {
        console.error("Error:", error);
        mostrarNotificacion("❌ Error al crear el pedido", "danger");
    }
}

// ============================================
// CARGAR PEDIDOS
// ============================================
async function cargarPedidos() {
    const contenedor = document.getElementById("contenedor-pedidos");
    
    if (!contenedor) return;
    
    try {
        const response = await fetch("/api/pedidos");
        
        if (!response.ok) {
            throw new Error(`Error ${response.status}: ${response.statusText}`);
        }
        
        pedidosData = await response.json();
        pedidosFiltrados = pedidosData;
        
        console.log("Pedidos cargados:", pedidosData.length);
        
        // Aplicar filtros actuales
        aplicarFiltros();
        
        // Actualizar estadísticas
        actualizarEstadisticas();
        
    } catch (error) {
        console.error("Error cargando pedidos:", error);
        contenedor.innerHTML = `
            <div class="col-12 text-center py-5">
                <div class="alert alert-danger">
                    ❌ Error al cargar los pedidos
                    <br>
                    <button onclick="cargarPedidos()" class="btn btn-sm btn-outline-danger mt-2">
                        🔄 Reintentar
                    </button>
                </div>
            </div>
        `;
    }
}

// ============================================
// MOSTRAR PEDIDOS
// ============================================
function mostrarPedidos(pedidos) {
    const contenedor = document.getElementById("contenedor-pedidos");
    
    if (pedidos.length === 0) {
        contenedor.innerHTML = `
            <div class="col-12 text-center py-5">
                <p class="text-muted fs-5">📋 No hay pedidos para mostrar</p>
            </div>
        `;
        return;
    }
    
    contenedor.innerHTML = pedidos.map(pedido => crearTarjetaPedido(pedido)).join("");
    
    // Agregar event listeners
    agregarEventListenersPedidos();
}

// ============================================
// CREAR TARJETA DE PEDIDO
// ============================================
function crearTarjetaPedido(pedido) {
    const fecha = new Date(pedido.fecha);
    const fechaFormato = fecha.toLocaleString('es-EC');
    
    const estadoClases = {
        'PENDIENTE': 'bg-warning text-dark',
        'EN_PREPARACION': 'bg-info text-white',
        'ENTREGADO': 'bg-success text-white',
        'PAGADO': 'bg-primary text-white',
        'CANCELADO': 'bg-danger text-white'
    };
    
    const estadoIconos = {
        'PENDIENTE': '🟡',
        'EN_PREPARACION': '🔵',
        'ENTREGADO': '🟢',
        'PAGADO': '✅',
        'CANCELADO': '🔴'
    };
    
    const claseEstado = estadoClases[pedido.estado] || 'bg-secondary text-white';
    const iconoEstado = estadoIconos[pedido.estado] || '⚪';
    
    const mesaNumero = pedido.mesa?.numero || 'N/A';
    const clienteNombre = pedido.cliente?.nombre || 'Consumidor Final';
    
    // Calcular cantidad total de items
    const totalItems = pedido.detalles?.reduce((sum, d) => sum + d.cantidad, 0) || 0;
    
    return `
        <div class="col-md-6 col-lg-4 mb-3">
            <div class="card h-100 shadow-sm">
                <div class="card-header ${claseEstado} d-flex justify-content-between align-items-center">
                    <span class="fw-bold">Pedido #${pedido.id}</span>
                    <span class="badge bg-dark">Mesa ${mesaNumero}</span>
                </div>
                <div class="card-body">
                    <p class="mb-2">
                        <strong>📅 Fecha:</strong> ${fechaFormato}
                    </p>
                    <p class="mb-2">
                        <strong>👤 Cliente:</strong> ${clienteNombre}
                    </p>
                    <p class="mb-2">
                        <strong>🍽️ Items:</strong> ${totalItems}
                    </p>
                    <p class="mb-2">
                        <strong>💵 Total:</strong> <span class="fs-5 text-success fw-bold">$${parseFloat(pedido.total).toFixed(2)}</span>
                    </p>
                    <p class="mb-3">
                        <strong>Estado:</strong> 
                        <span class="badge ${claseEstado}">${iconoEstado} ${pedido.estado}</span>
                    </p>
                    
                    <div class="d-flex gap-2">
                        <button class="btn btn-sm btn-primary flex-fill btn-ver-detalle" 
                                data-pedido-id="${pedido.id}">
                            👁️ Ver Detalle
                        </button>
                        ${pedido.estado !== 'PAGADO' && pedido.estado !== 'CANCELADO' ? `
                            <div class="btn-group flex-fill">
                                <button class="btn btn-sm btn-success btn-cambiar-estado" 
                                        data-pedido-id="${pedido.id}"
                                        data-estado="${getNextEstado(pedido.estado)}"
                                        title="Cambiar a ${getNextEstado(pedido.estado)}">
                                    ➡️
                                </button>
                                <button class="btn btn-sm btn-danger btn-cancelar-pedido" 
                                        data-pedido-id="${pedido.id}"
                                        title="Cancelar">
                                    ❌
                                </button>
                            </div>
                        ` : ''}
                    </div>
                </div>
            </div>
        </div>
    `;
}

// ============================================
// OBTENER SIGUIENTE ESTADO
// ============================================
function getNextEstado(estadoActual) {
    const flujo = {
        'PENDIENTE': 'EN_PREPARACION',
        'EN_PREPARACION': 'ENTREGADO',
        'ENTREGADO': 'PAGADO',
        'PAGADO': 'PAGADO',
        'CANCELADO': 'CANCELADO'
    };
    return flujo[estadoActual] || estadoActual;
}

// ============================================
// EVENT LISTENERS
// ============================================
function agregarEventListenersPedidos() {
    // Ver detalle
    document.querySelectorAll(".btn-ver-detalle").forEach(btn => {
        btn.addEventListener("click", () => {
            const pedidoId = btn.dataset.pedidoId;
            mostrarDetallePedido(pedidoId);
        });
    });
    
    // Cambiar estado
    document.querySelectorAll(".btn-cambiar-estado").forEach(btn => {
        btn.addEventListener("click", () => {
            const pedidoId = btn.dataset.pedidoId;
            const nuevoEstado = btn.dataset.estado;
            cambiarEstadoPedido(pedidoId, nuevoEstado);
        });
    });
    
    // Cancelar pedido
    document.querySelectorAll(".btn-cancelar-pedido").forEach(btn => {
        btn.addEventListener("click", () => {
            const pedidoId = btn.dataset.pedidoId;
            if (confirm("¿Está seguro de cancelar este pedido?")) {
                cambiarEstadoPedido(pedidoId, "CANCELADO");
            }
        });
    });
}

// ============================================
// MOSTRAR DETALLE DEL PEDIDO
// ============================================
function mostrarDetallePedido(pedidoId) {
    const pedido = pedidosData.find(p => p.id == pedidoId);
    
    if (!pedido) {
        alert("Pedido no encontrado");
        return;
    }
    
    const fecha = new Date(pedido.fecha).toLocaleString('es-EC');
    const mesaNumero = pedido.mesa?.numero || 'N/A';
    const clienteNombre = pedido.cliente?.nombre || 'Consumidor Final';
    
    let detallesHTML = '';
    if (pedido.detalles && pedido.detalles.length > 0) {
        detallesHTML = pedido.detalles.map(detalle => `
            <tr>
                <td>${detalle.producto?.nombre || 'Producto'}</td>
                <td class="text-center">${detalle.cantidad}</td>
                <td class="text-end">$${parseFloat(detalle.precioUnitario).toFixed(2)}</td>
                <td class="text-end fw-bold">$${detalle.calcularSubtotal ? detalle.calcularSubtotal().toFixed(2) : (detalle.cantidad * detalle.precioUnitario).toFixed(2)}</td>
            </tr>
        `).join('');
    } else {
        detallesHTML = '<tr><td colspan="4" class="text-center text-muted">Sin detalles</td></tr>';
    }
    
    const subtotal = pedido.total - pedido.iva;
    
    const content = `
        <div class="row">
            <div class="col-md-6">
                <p><strong>Pedido:</strong> #${pedido.id}</p>
                <p><strong>Fecha:</strong> ${fecha}</p>
                <p><strong>Estado:</strong> <span class="badge bg-primary">${pedido.estado}</span></p>
            </div>
            <div class="col-md-6">
                <p><strong>Mesa:</strong> ${mesaNumero}</p>
                <p><strong>Cliente:</strong> ${clienteNombre}</p>
            </div>
        </div>
        
        <hr>
        
        <h6 class="mb-3">📋 Detalles del Pedido:</h6>
        <table class="table table-sm">
            <thead>
                <tr>
                    <th>Producto</th>
                    <th class="text-center">Cant.</th>
                    <th class="text-end">Precio Unit.</th>
                    <th class="text-end">Subtotal</th>
                </tr>
            </thead>
            <tbody>
                ${detallesHTML}
            </tbody>
            <tfoot>
                <tr>
                    <td colspan="3" class="text-end"><strong>Subtotal:</strong></td>
                    <td class="text-end">$${subtotal.toFixed(2)}</td>
                </tr>
                <tr>
                    <td colspan="3" class="text-end"><strong>IVA (15%):</strong></td>
                    <td class="text-end">$${parseFloat(pedido.iva).toFixed(2)}</td>
                </tr>
                <tr class="table-primary">
                    <td colspan="3" class="text-end"><strong>TOTAL:</strong></td>
                    <td class="text-end"><strong>$${parseFloat(pedido.total).toFixed(2)}</strong></td>
                </tr>
            </tfoot>
        </table>
    `;
    
    document.getElementById("detalle-pedido-content").innerHTML = content;
    
    const modal = new bootstrap.Modal(document.getElementById("modal-detalle-pedido"));
    modal.show();
}

// ============================================
// CAMBIAR ESTADO DEL PEDIDO
// ============================================
async function cambiarEstadoPedido(pedidoId, nuevoEstado) {
    try {
        const response = await fetch(`/api/pedidos/${pedidoId}/estado`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(nuevoEstado)
        });
        
        if (response.ok) {
            mostrarNotificacion(`✅ Estado actualizado a ${nuevoEstado}`, "success");
            cargarPedidos();
        } else {
            const error = await response.text();
            mostrarNotificacion(`❌ Error: ${error}`, "danger");
        }
    } catch (error) {
        console.error("Error:", error);
        mostrarNotificacion("❌ Error de conexión", "danger");
    }
}

// ============================================
// FILTROS
// ============================================
function inicializarFiltros() {
    document.getElementById("filtroEstado")?.addEventListener("change", aplicarFiltros);
    document.getElementById("filtroMesa")?.addEventListener("change", aplicarFiltros);
}

function aplicarFiltros() {
    const filtroEstado = document.getElementById("filtroEstado")?.value;
    const filtroMesa = document.getElementById("filtroMesa")?.value;
    
    pedidosFiltrados = pedidosData.filter(pedido => {
        const cumpleEstado = !filtroEstado || pedido.estado === filtroEstado;
        const cumpleMesa = !filtroMesa || pedido.mesa?.numero == filtroMesa;
        return cumpleEstado && cumpleMesa;
    });
    
    mostrarPedidos(pedidosFiltrados);
}

// ============================================
// ESTADÍSTICAS
// ============================================
function actualizarEstadisticas() {
    const pendientes = pedidosData.filter(p => p.estado === 'PENDIENTE').length;
    const preparacion = pedidosData.filter(p => p.estado === 'EN_PREPARACION').length;
    const entregados = pedidosData.filter(p => p.estado === 'ENTREGADO').length;
    const totalDia = pedidosData.reduce((sum, p) => sum + (p.total || 0), 0);
    
    document.getElementById("stat-pendientes").textContent = pendientes;
    document.getElementById("stat-preparacion").textContent = preparacion;
    document.getElementById("stat-entregados").textContent = entregados;
    document.getElementById("stat-total").textContent = `$${totalDia.toFixed(2)}`;
}

// ============================================
// NOTIFICACIONES
// ============================================
function mostrarNotificacion(mensaje, tipo = "info") {
    const iconos = {
        success: "✅",
        danger: "❌",
        warning: "⚠️",
        info: "ℹ️"
    };

    const toastHTML = `
        <div class="toast align-items-center text-white bg-${tipo} border-0" role="alert">
            <div class="d-flex">
                <div class="toast-body">
                    ${iconos[tipo] || ""} ${mensaje}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" 
                        data-bs-dismiss="toast"></button>
            </div>
        </div>
    `;
    
    let toastContainer = document.getElementById("toast-container");
    if (!toastContainer) {
        toastContainer = document.createElement("div");
        toastContainer.id = "toast-container";
        toastContainer.className = "toast-container position-fixed top-0 end-0 p-3";
        toastContainer.style.zIndex = "9999";
        document.body.appendChild(toastContainer);
    }
    
    toastContainer.insertAdjacentHTML("beforeend", toastHTML);
    const toastElement = toastContainer.lastElementChild;
    
    if (typeof bootstrap !== "undefined") {
        const toast = new bootstrap.Toast(toastElement, { delay: 3000 });
        toast.show();
        toastElement.addEventListener("hidden.bs.toast", () => toastElement.remove());
    }
}

// ============================================
// EXPONER FUNCIONES GLOBALES
// ============================================
window.cargarPedidos = cargarPedidos;
window.abrirModalNuevoPedido = abrirModalNuevoPedido;
window.agregarAlCarrito = agregarAlCarrito;
window.cambiarCantidad = cambiarCantidad;
window.actualizarCantidad = actualizarCantidad;
window.eliminarDelCarrito = eliminarDelCarrito;
window.confirmarNuevoPedido = confirmarNuevoPedido;