let productoSeleccionadoId = null;

document.addEventListener("DOMContentLoaded", () => {
    cargarProductos();
});

// 1. Cargar productos desde el API
async function cargarProductos() {
    try {
        const response = await fetch("/api/productos");
        const productos = await response.json();
        mostrarProductos(productos);
    } catch (error) {
        console.error("Error al cargar productos:", error);
    }
}

// 2. Dibujar tarjetas en el HTML
function mostrarProductos(productos) {
    const contenedor = document.getElementById("contenedor-productos");
    contenedor.innerHTML = "";

    productos.forEach(p => {
        const div = document.createElement("div");
        div.className = "col-md-4 mb-4";
        div.innerHTML = `
            <div class="card h-100 card-custom shadow-sm">
                <div class="card-body">
                    <h5 class="card-title">${p.nombre}</h5>
                    <p class="card-text text-muted small">${p.descripcion || "Sin descripción"}</p>
                    <div class="d-flex justify-content-between align-items-center">
                        <span class="precio-tag">$${p.precio.toFixed(2)}</span>
                        <button class="btn btn-sm btn-primary rounded-pill px-3" 
                                onclick="abrirModalPedido(${p.id}, '${p.nombre}')">
                            Agregar
                        </button>
                    </div>
                </div>
            </div>
        `;
        contenedor.appendChild(div);
    });
}

// 3. Lógica del Modal
function abrirModalPedido(id, nombre) {
    productoSeleccionadoId = id;
    document.getElementById("producto-info").innerText = "Producto: " + nombre;
    document.getElementById("modal-pedido").style.display = "flex";
}

function cerrarModal() {
    document.getElementById("modal-pedido").style.display = "none";
}

// 4. Enviar POST al PedidoController
async function confirmarEnvioPedido() {
    const mesaId = document.getElementById("select-mesa").value;
    
    const pedidoDTO = {
        mesaId: parseInt(mesaId),
        productosIds: [productoSeleccionadoId],
        clienteId: 1 // Por ahora estático
    };

    try {
        const response = await fetch('/api/pedidos', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(pedidoDTO)
        });

        if (response.ok) {
            alert("✅ Pedido registrado en la base de datos.");
            cerrarModal();
        } else {
            alert("❌ Hubo un problema al crear el pedido.");
        }
    } catch (error) {
        console.error("Error:", error);
    }
}

// 4. Enviar POST al PedidoController con manejo de errores detallado
async function confirmarEnvioPedido() {
    const mesaId = document.getElementById("select-mesa").value;
    
    const pedidoDTO = {
        mesaId: parseInt(mesaId),
        productosIds: [parseInt(productoSeleccionadoId)], // Aseguramos que sea entero
        clienteId: 1 
    };

    try {
        const response = await fetch('/api/pedidos', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(pedidoDTO)
        });

        if (response.ok) {
            alert("✅ ¡Pedido enviado a cocina!");
            cerrarModal();
        } else {
            // Intentamos leer el mensaje de error del servidor
            const errorMsg = await response.text();
            alert("❌ Error: " + errorMsg || "No se pudo crear el pedido.");
            console.error("Detalle del error:", errorMsg);
        }
    } catch (error) {
        alert("❌ Error de conexión con el servidor.");
        console.error("Error:", error);
    }
}