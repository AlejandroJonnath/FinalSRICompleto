<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List, model.Usuario" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8" />
  <title>Administrar Usuarios</title>
  <link rel="stylesheet" href="css/usuarios.css" />
</head>
<body>

<%
  /* ================================
     1) AUTENTICACIÓN Y CONTROL DE ACCESO
     - Verifica que haya un usuario logueado y que sea ADMIN.
     - Si no está autenticado, redirige a login.
     - Si no es ADMIN, redirige a productos.
     ================================== */

  // Obtener el usuario de la sesión
  String usuarioLogueado = (String) session.getAttribute("usuario");

  // Obtener el rol del usuario
  String rol= (String) session.getAttribute("rol");

  // Si no hay usuario autenticado, redirigir a login
  if (usuarioLogueado == null) {
    response.sendRedirect("login");
    return; // Termina el procesamiento de la página
  }

  // Si el usuario no tiene rol ADMIN, redirigir a productos
  if (!"ADMIN".equals(rol)) {
    response.sendRedirect("productos");
    return; // Termina el procesamiento de la página
  }

  /*
   * IMPORTANTE: Si se modifica esta sección,
   * se puede comprometer la seguridad del panel.
   * Usuarios no autorizados podrían acceder a la administración.
   */

  /* ================================
     2) OBTENER DATOS Y MENSAJES DE RETROALIMENTACIÓN
     - Obtiene la lista de usuarios para mostrar.
     - Obtiene mensajes de error o éxito para crear, editar o eliminar.
     ================================== */

  @SuppressWarnings("unchecked")
  List<Usuario> usuarios = (List<Usuario>) request.getAttribute("usuarios");

  String errorCrear    = (String) request.getAttribute("errorCrear");
  String msgCrear      = (String) request.getAttribute("msgCrear");
  String errorEditar   = (String) request.getAttribute("errorEditar");
  String msgEditar     = (String) request.getAttribute("msgEditar");
  String errorEliminar = (String) request.getAttribute("errorEliminar");
  String msgEliminar   = (String) request.getAttribute("msgEliminar");
%>

<!-- ================================
     3) BARRA SUPERIOR CON INFO DE SESIÓN Y NAVEGACIÓN
     ================================== -->
<div class="top-bar">
  <div>
    Admin: <strong><%= usuarioLogueado %></strong> (<%= rol %>) <!-- Muestra el usuario y rol -->
  </div>
  <div>
    <a href="productos"><button type="button" class="btn">Volver a Productos</button></a> <!-- Botón volver -->
    <a href="logout"><button type="button" class="btn">Cerrar Sesión</button></a>         <!-- Botón logout -->
  </div>
</div>

<h2 class="titulo">Panel de Gestión de Usuarios</h2>

<!-- ================================
     4) MENSAJES DE ERROR O ÉXITO PARA OPERACIONES CRUD
     ================================== -->
<div class="mensajes">
  <% if (errorCrear != null) { %>
  <p class="error"><%= errorCrear %></p> <!-- Mostrar error creación -->
  <% } else if (msgCrear != null) { %>
  <p class="exito"><%= msgCrear %></p> <!-- Mostrar éxito creación -->
  <% } %>

  <% if (errorEditar != null) { %>
  <p class="error"><%= errorEditar %></p> <!-- Mostrar error edición -->
  <% } else if (msgEditar != null) { %>
  <p class="exito"><%= msgEditar %></p> <!-- Mostrar éxito edición -->
  <% } %>

  <% if (errorEliminar != null) { %>
  <p class="error"><%= errorEliminar %></p> <!-- Mostrar error eliminación -->
  <% } else if (msgEliminar != null) { %>
  <p class="exito"><%= msgEliminar %></p> <!-- Mostrar éxito eliminación -->
  <% } %>
</div>

<!-- ================================
     5) BOTÓN PARA MOSTRAR FORMULARIO CREAR NUEVO USUARIO
     ================================== -->
<div class="boton-anadir-contenedor">
  <button
          type="button"
          id="btnShowCreate"
          class="btn btn-anadir"
          onclick="showCreateForm()"
  >
    Añadir Usuario
  </button>
</div>

<!-- ================================
     6) FORMULARIO DE CREACIÓN DE USUARIO (INICIALMENTE OCULTO)
     ================================== -->
<div id="formCrear" class="form-crear oculto">
  <h3>Crear Nuevo Usuario</h3>
  <form action="usuarios" method="post" class="form-inner">
    <input type="hidden" name="action" value="crear" /> <!-- Indica acción crear al servlet -->

    <label for="nuevoUsuario">Usuario:</label>
    <input type="text" name="usuario" id="nuevoUsuario" /> <!-- Input para nombre de usuario -->

    <label for="nuevaContrasena">Contraseña:</label>
    <input type="password" name="contrasena" id="nuevaContrasena" /> <!-- Input para contraseña -->

    <div class="botones-form">
      <button type="submit" class="btn btn-crear">Guardar</button>  <!-- Botón para enviar form -->
      <button
              type="button"
              class="btn btn-cancelar"
              onclick="hideCreateForm()"
      >
        Cancelar
      </button> <!-- Botón para ocultar form sin enviar -->
    </div>
  </form>
</div>

<!-- ================================
     7) TABLA CON LISTA DE USUARIOS
     ================================== -->
<div class="tabla-contenedor">
  <table>
    <thead>
    <tr>
      <th>ID</th>
      <th>Usuario</th>
      <!-- <th>Contraseña</th> --> <!-- Contraseña no visible por seguridad -->
      <th>Acciones</th>
    </tr>
    </thead>
    <tbody>
    <%
      if (usuarios != null && !usuarios.isEmpty()) { // Si hay usuarios
        for (Usuario u : usuarios) {                  // Recorre cada usuario
    %>
    <tr>
      <td><%= u.getId() %></td>                   <!-- Muestra ID usuario -->
      <td><%= u.getUsuario() %></td>               <!-- Muestra nombre usuario -->
      <!--<td><%= u.getContrasena() %></td>-->       <!-- No se muestra contraseña -->

      <td>
        <!-- Botón Editar -->
        <button
                type="button"
                class="btn btn-editar"
                onclick="abrirEditarModal(
                  <%= u.getId() %>,
                        '<%= u.getUsuario().replace("'", "\\'") %>',
                        '<%= u.getContrasena().replace("'", "\\'") %>'
                        )"
        >
          Editar
        </button>

        <!-- Botón Eliminar -->
        <button
                type="button"
                class="btn btn-eliminar"
                onclick="abrirEliminarModal(<%= u.getId() %>)"
        >
          Eliminar
        </button>
      </td>
    </tr>
    <%
      }
    } else { // No hay usuarios registrados
    %>
    <tr>
      <td colspan="4" style="text-align:center; padding: 12px;">
        No hay usuarios registrados.
      </td>
    </tr>
    <%
      }
    %>
    </tbody>
  </table>
</div>

<!-- ================================
     8) MODAL PARA EDITAR USUARIO (INICIALMENTE OCULTO)
     ================================== -->
<div id="modalEditarUsuario" class="modal oculto">
  <div class="modal-contenido">
    <h3>Editar Usuario</h3>
    <form id="formEditar" action="usuarios" method="post" class="form-inner">
      <input type="hidden" name="action" value="editar" /> <!-- Acción editar -->
      <input type="hidden" name="id" id="editId" />        <!-- ID usuario a editar -->

      <label for="editUsuario">Usuario:</label>
      <input type="text" name="usuario" id="editUsuario" />  <!-- Nuevo nombre usuario -->

      <label for="editContrasena">Contraseña:</label>
      <input type="password" name="contrasena" id="editContrasena" /> <!-- Nueva contraseña -->

      <div class="botones-form">
        <button type="submit" class="btn btn-guardar">Guardar</button> <!-- Guardar cambios -->
        <button
                type="button"
                class="btn btn-cancelar"
                onclick="cerrarEditarModal()"
        >
          Cancelar
        </button> <!-- Cancelar edición -->
      </div>
    </form>
  </div>
</div>

<!-- ================================
     9) MODAL PARA CONFIRMAR ELIMINACIÓN DE USUARIO (INICIALMENTE OCULTO)
     ================================== -->
<div id="modalEliminarUsuario" class="modal oculto">
  <div class="modal-contenido">
    <h3>Confirmar Eliminación</h3>
    <p>¿Estás seguro de que deseas eliminar este usuario?</p>
    <form id="formEliminar" action="usuarios" method="post" class="form-inner">
      <input type="hidden" name="action" value="eliminar" /> <!-- Acción eliminar -->
      <input type="hidden" name="id" id="deleteId" />        <!-- ID usuario a eliminar -->
      <div class="botones-form">
        <button type="submit" class="btn btn-eliminar">Sí, eliminar</button> <!-- Confirmar -->
        <button
                type="button"
                class="btn btn-cancelar"
                onclick="cerrarEliminarModal()"
        >
          Cancelar
        </button> <!-- Cancelar eliminación -->
      </div>
    </form>
  </div>
</div>

<script src="js/usuarios.js"></script> <!-- Script para mostrar/ocultar formularios y modales -->

</body>
</html>
