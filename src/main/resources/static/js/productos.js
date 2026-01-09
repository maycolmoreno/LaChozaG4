// Variables globales
let productos = [];
let categorias = [];
let productoEditando = null;

// Inicialización
document.addEventListener('DOMContentLoaded', function() {
    console.log('Iniciando aplicación de productos...');
    cargarProductos();
    cargarCategorias();
    
    // Event listeners
    const btnNuevo = document.getElementById('btnNuevoProducto');
    if (btnNuevo) {
        btnNuevo.addEventListener('click', abrirModalNuevo);
    }
    
    const formProducto = document.getElementById('formProducto');
    if (formProducto) {
        formProducto.addEventListener('submit', guardarProducto);
    }
    
    const buscar = document.getElementById('buscarProducto');
    if (buscar) {
        buscar.addEventListener('input', filtrarProductos);
    }
    
    const filtro = document.getElementById('filtroCategoria');
    if (filtro) {
        filtro.addEventListener('change', filtrarProductos);
    }
});

// Cargar productos
async function cargarProductos() {
    console.log('Cargando productos...');
    try {
        const response = await fetch('/api/productos');
        console.log('Response status:', response.status);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        productos = await response.json();
        console.log('Productos cargados:', productos);
        mostrarProductos(productos);
    } catch (error) {
        console.error('Error al cargar productos:', error);
        const contenedor = document.getElementById('contenedor-productos');
        if (contenedor) {
            contenedor.innerHTML = `
                <div class="col-12">
                    <div class="alert alert-danger" role="alert">
                        <h4 class="alert-heading">❌ Error al cargar productos</h4>
                        <p>${error.message}</p>
                        <hr>
                        <button class="btn btn-danger" onclick="cargarProductos()">
                            🔄 Reintentar
                        </button>
                    </div>
                </div>
            `;
        }
    }
}

// Cargar categorías
async function cargarCategorias() {
    console.log('Cargando categorías...');
    try {
        const response = await fetch('/api/categorias');
        console.log('Response categorías status:', response.status);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        categorias = await response.json();
        console.log('Categorías cargadas:', categorias);
        
        // Llenar select de categorías
        const selectCategoria = document.getElementById('productoCategoria');
        const filtroCategoria = document.getElementById('filtroCategoria');
        
        if (selectCategoria) {
            selectCategoria.innerHTML = '<option value="">Seleccione una categoría</option>';
            categorias.forEach(cat => {
                selectCategoria.innerHTML += `<option value="${cat.id}">${cat.nombre}</option>`;
            });
        }
        
        if (filtroCategoria) {
            filtroCategoria.innerHTML = '<option value="">Todas las categorías</option>';
            categorias.forEach(cat => {
                filtroCategoria.innerHTML += `<option value="${cat.id}">${cat.nombre}</option>`;
            });
        }
    } catch (error) {
        console.error('Error al cargar categorías:', error);
    }
}

// Mostrar productos
function mostrarProductos(productos) {
    const contenedor = document.getElementById('contenedor-productos');
    
    if (!contenedor) {
        console.error('No se encontró el contenedor de productos');
        return;
    }
    
    if (productos.length === 0) {
        contenedor.innerHTML = `
            <div class="col-12 text-center py-5">
                <i class="fas fa-box-open fa-4x text-muted mb-3"></i>
                <h4 class="text-muted">No hay productos para mostrar</h4>
                <p class="text-muted">Comienza agregando tu primer producto</p>
                <button class="btn btn-primary btn-lg" onclick="abrirModalNuevo()">
                    ➕ Agregar primer producto
                </button>
            </div>
        `;
        return;
    }
    
    contenedor.innerHTML = productos.map(producto => `
        <div class="col-md-6 col-lg-4 col-xl-3 mb-4">
            <div class="card h-100 shadow-sm hover-shadow">
                ${producto.imagenUrl ? `
                    <img src="${producto.imagenUrl}" class="card-img-top" alt="${producto.nombre}" 
                         style="height: 200px; object-fit: cover;"
                         onerror="this.onerror=null; this.src='/images/default-product.jpg';">
                ` : `
                    <div class="card-img-top bg-light d-flex align-items-center justify-content-center" 
                         style="height: 200px;">
                        <i class="fas fa-utensils fa-3x text-muted"></i>
                    </div>
                `}
                <div class="card-body">
                    <h5 class="card-title text-truncate" title="${producto.nombre}">
                        ${producto.nombre}
                    </h5>
                    <p class="card-text text-muted small" style="height: 60px; overflow: hidden;">
                        ${producto.descripcion || 'Sin descripción'}
                    </p>
                    <div class="mb-2">
                        <span class="badge ${producto.disponible ? 'bg-success' : 'bg-danger'}">
                            ${producto.disponible ? '✓ Disponible' : '✗ No disponible'}
                        </span>
                        ${producto.categoria ? `
                            <span class="badge bg-info">${producto.categoria.nombre}</span>
                        ` : ''}
                    </div>
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <div>
                            <small class="text-muted d-block">Precio</small>
                            <span class="text-success fs-5 fw-bold">$${producto.precio.toFixed(2)}</span>
                        </div>
                        <div class="text-end">
                            <small class="text-muted d-block">Stock</small>
                            <span class="${producto.stockActual > 0 ? 'text-success' : 'text-danger'} fw-bold">
                                ${producto.stockActual}
                            </span>
                        </div>
                    </div>
                </div>
                <div class="card-footer bg-white border-top-0">
                    <div class="btn-group w-100" role="group">
                        <button class="btn btn-sm btn-outline-primary" onclick="editarProducto(${producto.id})"
                                title="Editar producto">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-danger" onclick="eliminarProducto(${producto.id}, '${producto.nombre.replace(/'/g, "\\'")}')"
                                title="Eliminar producto">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    `).join('');
}

// Filtrar productos
function filtrarProductos() {
    const busqueda = document.getElementById('buscarProducto')?.value.toLowerCase() || '';
    const categoriaId = document.getElementById('filtroCategoria')?.value || '';
    
    let productosFiltrados = [...productos];
    
    // Filtrar por búsqueda
    if (busqueda) {
        productosFiltrados = productosFiltrados.filter(p => 
            p.nombre.toLowerCase().includes(busqueda) ||
            (p.descripcion && p.descripcion.toLowerCase().includes(busqueda))
        );
    }
    
    // Filtrar por categoría
    if (categoriaId) {
        productosFiltrados = productosFiltrados.filter(p => 
            p.categoria && p.categoria.id == categoriaId
        );
    }
    
    mostrarProductos(productosFiltrados);
}

// Abrir modal para nuevo producto
function abrirModalNuevo() {
    productoEditando = null;
    document.getElementById('modalProductoTitle').textContent = '➕ Nuevo Producto';
    document.getElementById('formProducto').reset();
    document.getElementById('productoDisponible').checked = true;
    
    const modal = new bootstrap.Modal(document.getElementById('modalProducto'));
    modal.show();
}

// Editar producto
async function editarProducto(id) {
    console.log('Editando producto:', id);
    try {
        const response = await fetch(`/api/productos/${id}`);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        productoEditando = await response.json();
        console.log('Producto a editar:', productoEditando);
        
        document.getElementById('modalProductoTitle').textContent = '✏️ Editar Producto';
        document.getElementById('productoNombre').value = productoEditando.nombre;
        document.getElementById('productoDescripcion').value = productoEditando.descripcion || '';
        document.getElementById('productoPrecio').value = productoEditando.precio;
        document.getElementById('productoStock').value = productoEditando.stockActual;
        document.getElementById('productoCategoria').value = productoEditando.categoria?.id || '';
        document.getElementById('productoDisponible').checked = productoEditando.disponible;
        document.getElementById('productoImagenUrl').value = productoEditando.imagenUrl || '';
        
        const modal = new bootstrap.Modal(document.getElementById('modalProducto'));
        modal.show();
    } catch (error) {
        console.error('Error al cargar el producto:', error);
        alert('❌ Error al cargar el producto: ' + error.message);
    }
}

// Guardar producto
async function guardarProducto(e) {
    e.preventDefault();
    
    console.log('Guardando producto...');
    
    const categoriaId = document.getElementById('productoCategoria').value;
    
    if (!categoriaId) {
        alert('⚠️ Debe seleccionar una categoría');
        return;
    }
    
    const categoria = categorias.find(c => c.id == categoriaId);
    
    const producto = {
        nombre: document.getElementById('productoNombre').value.trim(),
        descripcion: document.getElementById('productoDescripcion').value.trim(),
        precio: parseFloat(document.getElementById('productoPrecio').value),
        stockActual: parseInt(document.getElementById('productoStock').value),
        categoria: categoria,
        disponible: document.getElementById('productoDisponible').checked,
        imagenUrl: document.getElementById('productoImagenUrl').value.trim() || null
    };
    
    console.log('Datos del producto:', producto);
    
    try {
        const url = productoEditando 
            ? `/api/productos/${productoEditando.id}` 
            : '/api/productos';
        
        const method = productoEditando ? 'PUT' : 'POST';
        
        console.log(`${method} ${url}`);
        
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(producto)
        });
        
        console.log('Response status:', response.status);
        
        if (!response.ok) {
            const errorText = await response.text();
            console.error('Error response:', errorText);
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        // Cerrar modal
        const modal = bootstrap.Modal.getInstance(document.getElementById('modalProducto'));
        modal.hide();
        
        // Recargar productos
        await cargarProductos();
        
        // Mostrar mensaje de éxito
        mostrarNotificacion(
            productoEditando ? '✓ Producto actualizado exitosamente' : '✓ Producto creado exitosamente',
            'success'
        );
    } catch (error) {
        console.error('Error al guardar el producto:', error);
        mostrarNotificacion('❌ Error al guardar el producto: ' + error.message, 'danger');
    }
}

// Eliminar producto
async function eliminarProducto(id, nombre) {
    if (!confirm(`¿Está seguro de eliminar el producto "${nombre}"?\n\nEsta acción no se puede deshacer.`)) {
        return;
    }
    
    console.log('Eliminando producto:', id);
    
    try {
        const response = await fetch(`/api/productos/${id}`, {
            method: 'DELETE'
        });
        
        console.log('Response status:', response.status);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        await cargarProductos();
        mostrarNotificacion('✓ Producto eliminado exitosamente', 'success');
    } catch (error) {
        console.error('Error al eliminar el producto:', error);
        mostrarNotificacion('❌ Error al eliminar el producto: ' + error.message, 'danger');
    }
}

// Mostrar notificación (puedes mejorar esto con toasts de Bootstrap)
function mostrarNotificacion(mensaje, tipo) {
    alert(mensaje);
    // TODO: Implementar con toasts de Bootstrap para mejor UX
}

// Agregar estilos para hover en las cards
const style = document.createElement('style');
style.textContent = `
    .hover-shadow {
        transition: all 0.3s ease;
    }
    .hover-shadow:hover {
        transform: translateY(-5px);
        box-shadow: 0 0.5rem 1rem rgba(0,0,0,0.15) !important;
    }
`;
document.head.appendChild(style);
