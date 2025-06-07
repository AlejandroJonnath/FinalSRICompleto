// =========================================================
// SECCIÓN: Reactivación de Productos Inactivos (Administrador)
// =========================================================

// Variable global que almacena el ID del producto que se desea reactivar
let productoIdAReactivar = null;

// =========================================================
// FUNCIÓN: confirmarReactivacion(id)
// Despliega el modal de confirmación para reactivar un producto
// =========================================================
function confirmarReactivacion(id) {
    productoIdAReactivar = id; // Guarda el ID del producto seleccionado
    document.getElementById("modalReactivar").style.display = "flex"; // Muestra el modal cambiando su estilo a 'flex'
}

// 🔴 Si se modifica esta función para no guardar el ID correctamente,
//    la reactivación no funcionará porque no sabrá qué producto reactivar.

// =========================================================
// FUNCIÓN: cerrarModal()
// Cierra el modal de reactivación y limpia la variable de ID
// =========================================================
function cerrarModal() {
    productoIdAReactivar = null; // Limpia la variable para evitar errores si se intenta reactivar sin seleccionar producto
    document.getElementById("modalReactivar").style.display = "none"; // Oculta el modal cambiando su estilo a 'none'
}

// 🟡 Si no se limpia la variable al cerrar el modal, podría reactivarse un producto anterior por error.

// =========================================================
// FUNCIÓN: reactivarProducto()
// Redirige a la URL que activa el producto si hay un ID válido
// =========================================================
function reactivarProducto() {
    if (productoIdAReactivar !== null) { // Verifica si hay un producto seleccionado
        window.location.href = "activateProduct?id=" + productoIdAReactivar; // Redirige al endpoint con el ID para reactivar
    }
}

// 🔴 Si se cambia la verificación de null o la redirección, la acción puede no ejecutarse correctamente
//     o incluso enviar una URL inválida que cause error en el servidor.
