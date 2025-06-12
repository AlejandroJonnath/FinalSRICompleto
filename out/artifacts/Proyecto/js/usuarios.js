// =========================================================
// SECCIÓN 1: Mostrar / Ocultar Formulario de Creación de Usuario
// =========================================================

// Muestra el formulario de creación y desactiva el botón "Crear Usuario"
function showCreateForm() {
    document.getElementById("formCrear").classList.remove("oculto"); // Quita la clase 'oculto' para mostrar el formulario
    document.getElementById("btnShowCreate").disabled = true;        // Desactiva el botón para evitar múltiples clics
}

// Oculta el formulario de creación y reactiva el botón
function hideCreateForm() {
    document.getElementById("formCrear").classList.add("oculto");    // Agrega la clase 'oculto' para esconder el formulario
    document.getElementById("btnShowCreate").disabled = false;       // Activa el botón para permitir mostrar el formulario otra vez
}

// 🔴 Impacto: Si se elimina la desactivación/reactivación del botón, se podría mostrar el formulario varias veces innecesariamente.

// =========================================================
// SECCIÓN 2: Apertura y Cierre del Modal de Edición
// =========================================================

// Abre el modal de edición de usuario, cargando los valores actuales en el formulario
function abrirEditarModal(id, usuario, contrasena) {
    document.getElementById("editId").value = id;                    // Asigna el ID del usuario al input oculto
    document.getElementById("editUsuario").value = usuario;          // Asigna el nombre de usuario al input visible
    document.getElementById("editContrasena").value = contrasena;    // Asigna la contraseña al input (idealmente debería ir cifrada)
    document.getElementById("modalEditarUsuario").classList.remove("oculto"); // Muestra el modal quitando la clase 'oculto'
}

// Cierra el modal de edición
function cerrarEditarModal() {
    document.getElementById("modalEditarUsuario").classList.add("oculto"); // Oculta el modal agregando la clase 'oculto'
}

// 🔴 Impacto: Si no se cargan correctamente los valores en `abrirEditarModal`, se editaría otro usuario accidentalmente.

// =========================================================
// SECCIÓN 3: Apertura y Cierre del Modal de Eliminación
// =========================================================

// Abre el modal de eliminación y carga el ID del usuario a eliminar
function abrirEliminarModal(id) {
    document.getElementById("deleteId").value = id;                  // Coloca el ID en el campo oculto para enviarlo en el form
    document.getElementById("modalEliminarUsuario").classList.remove("oculto"); // Muestra el modal de confirmación
}

// Cierra el modal de eliminación
function cerrarEliminarModal() {
    document.getElementById("modalEliminarUsuario").classList.add("oculto"); // Oculta el modal de confirmación
}

// 🔴 Impacto: Si se elimina la asignación del ID en `abrirEliminarModal`, el formulario de eliminación no sabría qué usuario eliminar.
