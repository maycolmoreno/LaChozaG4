let clienteSeleccionadoId = null;

document.addEventListener("DOMContentLoaded", () => {
    cargarClientes();
});

// 1. Obtener clientes del API
async function cargarClientes() {
    try {
        const response = await fetch("/api/clientes");
        const clientes = await response.json();
        mostrarClientes(clientes);
    } catch (error) {
        console.error("❌ Error al cargar clientes:", error);
    }
}

// 2. Dibujar la tabla
function mostrarClientes(clientes) {
    const tabla = document.getElementById("tablaClientes");
    tabla.innerHTML = "";

    clientes.forEach(c => {
        tabla.innerHTML += `
            <tr>
                <td>${c.id}</td>
                <td>${c.nombre}</td>
                <td>${c.identificacion || "N/A"}</td>
                <td>${c.telefono || "N/A"}</td>
                <td>${c.email || "N/A"}</td>
                <td>${c.direccion || "N/A"}</td>
                <td>
                    <button class="btn btn-sm btn-primary" onclick="abrirModalEditarCliente(${c.id})">
                        <i class="bi bi-pencil"></i> Editar
                    </button>
                </td>
            </tr>
        `;
    });
}

// 3. Abrir modal en modo NUEVO
function abrirModalNuevoCliente() {
    clienteSeleccionadoId = null;
    document.getElementById("modalClienteTitulo").innerText = "Nuevo Cliente";

    document.getElementById("cliNombre").value = "";
    document.getElementById("cliIdentificacion").value = "";
    document.getElementById("cliTelefono").value = "";
    document.getElementById("cliEmail").value = "";
    document.getElementById("cliDireccion").value = "";

    new bootstrap.Modal(document.getElementById("modalCliente")).show();
}

// 4. Abrir modal en modo EDITAR
async function abrirModalEditarCliente(id) {
    try {
        const response = await fetch(`/api/clientes/${id}`);
        const cliente = await response.json();

        clienteSeleccionadoId = cliente.id;
        document.getElementById("modalClienteTitulo").innerText = "Editar Cliente";

        document.getElementById("cliNombre").value = cliente.nombre;
        document.getElementById("cliIdentificacion").value = cliente.identificacion;
        document.getElementById("cliTelefono").value = cliente.telefono;
        document.getElementById("cliEmail").value = cliente.email;
        document.getElementById("cliDireccion").value = cliente.direccion;

        new bootstrap.Modal(document.getElementById("modalCliente")).show();

    } catch (error) {
        console.error("❌ Error al cargar cliente para edición:", error);
        alert("No se pudo cargar el cliente.");
    }
}

// 5. Enviar datos (POST o PUT)
document.getElementById("formCliente").addEventListener("submit", async (e) => {
    e.preventDefault();

    const cliente = {
        nombre: document.getElementById("cliNombre").value,
        identificacion: document.getElementById("cliIdentificacion").value,
        telefono: parseInt(document.getElementById("cliTelefono").value),
        email: document.getElementById("cliEmail").value,
        direccion: document.getElementById("cliDireccion").value
    };

    try {
        const url = clienteSeleccionadoId
            ? `/api/clientes/${clienteSeleccionadoId}`
            : "/api/clientes";

        const method = clienteSeleccionadoId ? "PUT" : "POST";

        const response = await fetch(url, {
            method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(cliente)
        });

        if (response.ok) {
            alert("✅ Cliente guardado correctamente");
            cargarClientes();
            bootstrap.Modal.getInstance(document.getElementById("modalCliente")).hide();
        } else {
            const err = await response.text();
            alert("❌ Error: " + err);
            console.error("Error server:", err);
        }

    } catch (error) {
        console.error("❌ Error de conexión:", error);
        alert("Error de conexión con el servidor");
    }
});
