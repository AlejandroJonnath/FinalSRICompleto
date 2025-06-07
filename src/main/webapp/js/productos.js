// =========================================================
// SECCIÓN: Variables globales
// =========================================================

let productoIdAEliminar = null; // Variable global que guarda el ID del producto a eliminar (evita eliminar sin confirmar)

// =========================================================
// SECCIÓN: Función para mostrar el modal de confirmación de eliminación
// =========================================================

function confirmarEliminacion(id) {
    productoIdAEliminar = id; // Guarda el ID del producto que se quiere eliminar
    document.getElementById('modalEliminar').style.display = 'flex'; // Muestra el modal de eliminación usando CSS (display: flex)
}

// 🔴 Impacto: Si esta función se modifica incorrectamente, el modal no se mostrará, y el producto no se marcará para eliminación.

// =========================================================
// SECCIÓN: Función para cerrar el modal de eliminación
// =========================================================

function cerrarModal() {
    productoIdAEliminar = null; // Limpia el ID seleccionado
    document.getElementById('modalEliminar').style.display = 'none'; // Oculta el modal de confirmación
}

// 🔴 Impacto: Si no se limpia `productoIdAEliminar`, podría eliminarse un producto anterior por error.

// =========================================================
// SECCIÓN: Función que redirige al backend para eliminar el producto
// =========================================================

function eliminarProducto() {
    if (productoIdAEliminar !== null) { // Verifica que se haya seleccionado un producto
        window.location.href = 'deleteProduct?id=' + productoIdAEliminar; // Redirige al servlet con el ID del producto
    }
}

// 🔴 Impacto: Si se cambia el nombre del parámetro `id`, el backend no recibirá correctamente el producto a eliminar.

// =========================================================
// SECCIÓN: Validación de cantidad al comprar
// =========================================================

function validarCantidad(form) {
    const cantidadInput = form.querySelector('input[name="cantidad"]'); // Obtiene el input de cantidad dentro del formulario
    const cantidad = parseInt(cantidadInput.value); // Convierte el valor ingresado a número entero
    const stock = parseInt(cantidadInput.getAttribute('data-stock')); // Obtiene el stock máximo desde el atributo personalizado

    // Verifica que la cantidad sea válida (no vacía, no negativa, mayor a 0)
    if (isNaN(cantidad) || cantidad < 1) {
        alert("Ingresa una cantidad válida (mayor o igual a 1)."); // Muestra alerta si no es válida
        return false; // Detiene el envío del formulario
    }

    // Verifica que la cantidad no supere el stock disponible
    if (cantidad > stock) {
        document.getElementById('modalStockError').style.display = 'flex'; // Muestra modal de error si excede stock
        return false; // Cancela el envío del formulario
    }

    return true; // Todo correcto, permite enviar el formulario
}

// 🔴 Impacto: Si se elimina esta validación, los usuarios podrían intentar comprar cantidades mayores al stock disponible,
// causando errores en el backend y desbordes lógicos.

// =========================================================
// SECCIÓN: Cierra el modal de stock excedido
// =========================================================

function cerrarModalStock() {
    document.getElementById('modalStockError').style.display = 'none'; // Oculta el modal de error de stock
}

// 🔴 Impacto: Si se elimina, el modal de stock no se podrá cerrar y afectará la experiencia de usuario.
