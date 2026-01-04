// ============================================
// VARIABLES GLOBALES
// ============================================
let productoSeleccionadoId = null;
let pedidoActual = null;

// ============================================
// INICIALIZACIÓN
// ============================================
document.addEventListener("DOMContentLoaded", () => {
    console.log("DOM cargado, inicializando...");
    
    // Cargar productos si estamos en la página de listado
    const contenedorProductos = document.getElementById("contenedor-productos");
    if (contenedorProductos) {
        console.log("Página de productos detectada");
        cargarProductos();
    }
    
    // Cargar categorías si estamos en el formulario
    const selectCategoria = document.getElementById("prodCategoria");
    if (selectCategoria) {
        console.log("Formulario detectado, cargando categorías...");
        cargarCategorias();
    }
    
    // Inicializar formulario si existe
    inicializarFormulario();
});

// ============================================
// CARGA DE PRODUCTOS
// ============================================
async function cargarProductos() {
    const contenedor = document.getElementById("contenedor-productos");
    
    if (!contenedor) return;
    
    try {
        contenedor.innerHTML = `
            <div class="col-12 text-center py-5">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Cargando...</span>
                </div>
                <p class="mt-3">Cargando productos...</p>
            </div>
        `;

        const response = await fetch("/api/productos");
        
        if (!response.ok) {
            throw new Error(`Error ${response.status}: ${response.statusText}`);
        }

        const productos = await response.json();

        if (productos.length === 0) {
            contenedor.innerHTML = `
                <div class="col-12 text-center py-5">
                    <p class="text-muted fs-5">📋 No hay productos disponibles</p>
                    <a href="/agregar-producto" class="btn btn-primary mt-3">
                        ➕ Agregar primer producto
                    </a>
                </div>
            `;
            return;
        }

        // Agrupar productos por categoría
        const productosPorCategoria = agruparPorCategoria(productos);
        
        contenedor.innerHTML = "";
        
        for (const [categoria, items] of Object.entries(productosPorCategoria)) {
            contenedor.innerHTML += `
                <div class="col-12 mb-4">
                    <h2 class="categoria-titulo">${categoria}</h2>
                    <div class="row g-3">
                        ${items.map(producto => crearTarjetaProducto(producto)).join("")}
                    </div>
                </div>
            `;
        }

        // Agregar event listeners a los botones
        agregarEventListenersProductos();

    } catch (error) {
        console.error("Error cargando productos:", error);
        contenedor.innerHTML = `
            <div class="col-12 text-center py-5">
                <div class="alert alert-danger" role="alert">
                    ❌ Error al cargar los productos: ${error.message}
                    <br>
                    <button onclick="cargarProductos()" class="btn btn-sm btn-outline-danger mt-3">
                        🔄 Reintentar
                    </button>
                </div>
            </div>
        `;
    }
}

// ============================================
// AGRUPAR PRODUCTOS POR CATEGORÍA
// ============================================
function agruparPorCategoria(productos) {
    const grupos = {};
    
    productos.forEach(producto => {
        const categoria = producto.categoria?.nombre || "Sin categoría";
        if (!grupos[categoria]) {
            grupos[categoria] = [];
        }
        grupos[categoria].push(producto);
    });
    
    return grupos;
}

// ============================================
// CREAR TARJETA DE PRODUCTO
// ============================================
function crearTarjetaProducto(producto) {
    const imagenUrl = producto.imagenUrl || "/images/default-product.jpg";
    const precio = parseFloat(producto.precio).toFixed(2);
    const disponible = producto.disponible !== false && producto.activo !== false;
    const stockBadge = producto.stockActual > 0 
        ? `<span class="badge bg-success">Stock: ${producto.stockActual}</span>` 
        : `<span class="badge bg-danger">Sin stock</span>`;
    
    return `
        <div class="col-md-6 col-lg-4">
            <div class="card producto-card h-100 shadow-sm ${!disponible ? 'opacity-50' : ''}">
                <img src="${imagenUrl}" 
                     class="card-img-top producto-imagen" 
                     alt="${producto.nombre}"
                     onerror="this.src='/images/default-product.jpg'">
                <div class="card-body d-flex flex-column">
                    <div class="d-flex justify-content-between align-items-start mb-2">
                        <h5 class="card-title mb-0">${producto.nombre}</h5>
                        ${stockBadge}
                    </div>
                    <p class="card-text text-muted small flex-grow-1">
                        ${producto.descripcion || "Delicioso platillo de nuestra casa"}
                    </p>
                    ${!disponible ? '<p class="text-danger fw-bold">⚠️ No disponible</p>' : ''}
                    <div class="d-flex justify-content-between align-items-center mt-3">
                        <span class="precio-tag fw-bold text-success fs-5">$${precio}</span>
                        <div class="btn-group">
                            <button class="btn btn-primary btn-sm btn-pedir" 
                                    data-id="${producto.id}"
                                    data-nombre="${producto.nombre}"
                                    data-precio="${precio}"
                                    ${!disponible || producto.stockActual <= 0 ? 'disabled' : ''}>
                                🛒 Pedir
                            </button>
                            <button class="btn btn-outline-secondary btn-sm btn-editar" 
                                    data-id="${producto.id}">
                                ✏️
                            </button>
                            <button class="btn btn-outline-danger btn-sm btn-eliminar" 
                                    data-id="${producto.id}"
                                    data-nombre="${producto.nombre}">
                                🗑️
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
}

// ============================================
// EVENT LISTENERS DE PRODUCTOS
// ============================================
function agregarEventListenersProductos() {
    // Botones de pedir
    document.querySelectorAll(".btn-pedir").forEach(btn => {
        btn.addEventListener("click", () => {
            const id = btn.dataset.id;
            const nombre = btn.dataset.nombre;
            const precio = btn.dataset.precio;
            abrirModalPedido(id, nombre, precio);
        });
    });

    // Botones de editar
    document.querySelectorAll(".btn-editar").forEach(btn => {
        btn.addEventListener("click", () => {
            const id = btn.dataset.id;
            window.location.href = `/editar-producto/${id}`;
        });
    });

    // Botones de eliminar
    document.querySelectorAll(".btn-eliminar").forEach(btn => {
        btn.addEventListener("click", () => {
            const id = btn.dataset.id;
            const nombre = btn.dataset.nombre;
            confirmarEliminarProducto(id, nombre);
        });
    });
}

// ============================================
// MODAL DE PEDIDO
// ============================================
function abrirModalPedido(id, nombre, precio) {
    pedidoActual = { id, nombre, precio };
    
    document.getElementById("producto-info").textContent = 
        `${nombre} - $${precio}`;
    
    document.getElementById("modal-pedido").style.display = "flex";
}

function cerrarModal() {
    document.getElementById("modal-pedido").style.display = "none";
    pedidoActual = null;
}

async function confirmarEnvioPedido() {
    if (!pedidoActual) return;

    const mesa = document.getElementById("select-mesa").value;
    
    const pedido = {
        productoId: pedidoActual.id,
        mesa: parseInt(mesa),
        cantidad: 1,
        fecha: new Date().toISOString()
    };

    try {
        const response = await fetch("/api/pedidos", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(pedido)
        });

        if (response.ok) {
            mostrarNotificacion(
                `✅ Pedido enviado a la Mesa ${mesa}`, 
                "success"
            );
            cerrarModal();
        } else {
            const error = await response.text();
            mostrarNotificacion(`❌ Error: ${error}`, "danger");
        }
    } catch (error) {
        console.error("Error enviando pedido:", error);
        mostrarNotificacion(
            "⚠️ Sistema de pedidos no disponible aún", 
            "warning"
        );
        cerrarModal();
    }
}

// ============================================
// ELIMINAR PRODUCTO
// ============================================
function confirmarEliminarProducto(id, nombre) {
    if (confirm(`¿Está seguro de eliminar "${nombre}"?\n\nEsta acción no se puede deshacer.`)) {
        eliminarProducto(id);
    }
}

async function eliminarProducto(id) {
    try {
        const response = await fetch(`/api/productos/${id}`, {
            method: "DELETE"
        });

        if (response.ok || response.status === 204) {
            mostrarNotificacion("✅ Producto eliminado correctamente", "success");
            cargarProductos();
        } else if (response.status === 404) {
            mostrarNotificacion("⚠️ El producto ya no existe", "warning");
            cargarProductos();
        } else {
            const error = await response.text();
            mostrarNotificacion(`❌ Error: ${error}`, "danger");
        }
    } catch (error) {
        console.error("Error eliminando producto:", error);
        mostrarNotificacion("❌ Error de conexión", "danger");
    }
}

// ============================================
// FORMULARIO DE PRODUCTO (agregar/editar)
// ============================================
function inicializarFormulario() {
    const form = document.getElementById("formProducto");
    
    if (!form) return;
    
    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const nombre = document.getElementById("prodNombre").value.trim();
        const precio = parseFloat(document.getElementById("prodPrecio").value);
        const categoriaId = document.getElementById("prodCategoria").value;
        const imagenUrl = document.getElementById("prodImagen")?.value.trim() || "";
        const descripcion = document.getElementById("prodDescripcion")?.value.trim() || "";
        const stockActual = parseInt(document.getElementById("prodStock")?.value) || 0;

        // Validaciones
        if (!nombre || nombre.length < 3) {
            mostrarNotificacion("El nombre debe tener al menos 3 caracteres", "warning");
            return;
        }

        if (!precio || precio <= 0) {
            mostrarNotificacion("El precio debe ser mayor a 0", "warning");
            return;
        }

        if (!categoriaId) {
            mostrarNotificacion("Debe seleccionar una categoría", "warning");
            return;
        }

        const producto = {
            nombre,
            precio,
            descripcion,
            imagenUrl,
            stockActual,
            disponible: true,
            categoria: {
                id: parseInt(categoriaId)
            }
        };

        const btnSubmit = e.target.querySelector('button[type="submit"]');
        const textoOriginal = btnSubmit.innerHTML;
        btnSubmit.disabled = true;
        btnSubmit.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Guardando...';

        try {
            const esEdicion = productoSeleccionadoId != null;
            
            const url = esEdicion 
                ? `/api/productos/${productoSeleccionadoId}`
                : "/api/productos";

            const method = esEdicion ? "PUT" : "POST";

            const response = await fetch(url, {
                method,
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(producto)
            });

            if (response.ok || response.status === 201) {
                const accion = esEdicion ? "actualizado" : "creado";
                mostrarNotificacion(`✅ Producto ${accion} correctamente`, "success");
                
                setTimeout(() => {
                    window.location.href = "/producto";
                }, 1500);
            } else {
                const errorText = await response.text();
                mostrarNotificacion(`❌ Error: ${errorText}`, "danger");
            }

        } catch (error) {
            console.error("Error:", error);
            mostrarNotificacion("❌ Error de conexión con el servidor", "danger");
        } finally {
            btnSubmit.disabled = false;
            btnSubmit.innerHTML = textoOriginal;
        }
    });
    
    // Vista previa de imagen
    const inputImagen = document.getElementById("prodImagen");
    if (inputImagen) {
        inputImagen.addEventListener("input", function(e) {
            const url = e.target.value;
            const preview = document.getElementById("preview-imagen");
            const img = document.getElementById("img-preview");
            
            if (url) {
                img.src = url;
                preview.style.display = "block";
                
                img.onerror = function() {
                    preview.style.display = "none";
                };
            } else {
                preview.style.display = "none";
            }
        });
    }
}

// ============================================
// CARGAR CATEGORÍAS DINÁMICAMENTE
// ============================================
async function cargarCategorias() {
    const select = document.getElementById("prodCategoria");
    
    if (!select) {
        console.log("No se encontró el select de categorías");
        return;
    }
    
    console.log("Cargando categorías desde /api/categorias...");
    
    try {
        const response = await fetch("/api/categorias");
        
        console.log("Response status:", response.status);
        
        if (!response.ok) {
            throw new Error(`Error ${response.status}: ${response.statusText}`);
        }
        
        const categorias = await response.json();
        
        console.log("Categorías recibidas:", categorias);
        
        // Guardar la opción por defecto
        const defaultOption = '<option value="">-- Seleccione una categoría --</option>';
        
        // Limpiar y agregar opciones
        select.innerHTML = defaultOption;
        
        if (categorias.length === 0) {
            select.innerHTML = defaultOption + '<option value="" disabled>No hay categorías disponibles</option>';
            mostrarNotificacion("⚠️ No hay categorías creadas. Créalas primero.", "warning");
            return;
        }
        
        categorias.forEach(cat => {
            const option = document.createElement("option");
            option.value = cat.id;
            option.textContent = cat.nombre;
            select.appendChild(option);
        });
        
        console.log("✅ Categorías cargadas exitosamente:", categorias.length);
        
    } catch (error) {
        console.error("Error cargando categorías:", error);
        mostrarNotificacion(`⚠️ Error al cargar categorías: ${error.message}`, "warning");
        select.innerHTML = '<option value="">Error al cargar categorías</option>';
    }
}

// ============================================
// SISTEMA DE NOTIFICACIONES (TOAST)
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
    } else {
        toastElement.style.display = "block";
        setTimeout(() => toastElement.remove(), 3000);
    }
}

// ============================================
// EXPONER FUNCIONES GLOBALES
// ============================================
window.cerrarModal = cerrarModal;
window.confirmarEnvioPedido = confirmarEnvioPedido;
window.cargarProductos = cargarProductos;
window.cargarCategorias = cargarCategorias;