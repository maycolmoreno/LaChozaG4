// ============================================
// VARIABLES GLOBALES
// ============================================
let pedidosData = [];
let pedidosFiltrados = [];
let mesasData = [];

// ============================================
// INICIALIZACIÓN
// ============================================
document.addEventListener("DOMContentLoaded", () => {
    console.log("Iniciando módulo de pedidos...");
    
    // Solo ejecutar si estamos en la página de pedidos
    if (document.getElementById("contenedor-pedidos")) {
        cargarMesas();
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