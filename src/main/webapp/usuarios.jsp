<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List, model.Usuario" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Administrar Usuarios</title>
  <link rel="stylesheet" href="css/usuarios.css" />
</head>
<body>
<%
  // 1. Verificar autenticación y rol ADMIN
  String usuarioLogueado = (String) session.getAttribute("usuario");
  String rol             = (String) session.getAttribute("rol");
  if (usuarioLogueado == null) {
    response.sendRedirect("login");
    return;
  }
  if (!"ADMIN".equals(rol)) {
    response.sendRedirect("productos");
    return;
  }

  // 2. Obtener la lista de usuarios que el servlet puso en request
  @SuppressWarnings("unchecked")
  List<Usuario> usuarios = (List<Usuario>) request.getAttribute("usuarios");

  // 3. Mensajes de retroalimentación
  String errorCrear    = (String) request.getAttribute("errorCrear");
  String msgCrear      = (String) request.getAttribute("msgCrear");
  String errorEditar   = (String) request.getAttribute("errorEditar");
  String msgEditar     = (String) request.getAttribute("msgEditar");
  String errorEliminar = (String) request.getAttribute("errorEliminar");
  String msgEliminar   = (String) request.getAttribute("msgEliminar");
%>

<!-- Barra superior -->
<div class="top-bar">
  <div>
    Admin: <strong><%= usuarioLogueado %></strong> (<%= rol %>)
  </div>
  <div>
    <a href="productos"><button type="button" class="btn">Volver a Productos</button></a>
    <a href="logout"><button type="button" class="btn">Cerrar Sesión</button></a>
  </div>
</div>

<h2 class="titulo">Panel de Gestión de Usuarios</h2>

<!-- Mensajes de feedback -->
<div class="mensajes">
  <% if (errorCrear != null) { %>
  <p class="error"><%= errorCrear %></p>
  <% } else if (msgCrear != null) { %>
  <p class="exito"><%= msgCrear %></p>
  <% } %>

  <% if (errorEditar != null) { %>
  <p class="error"><%= errorEditar %></p>
  <% } else if (msgEditar != null) { %>
  <p class="exito"><%= msgEditar %></p>
  <% } %>

  <% if (errorEliminar != null) { %>
  <p class="error"><%= errorEliminar %></p>
  <% } else if (msgEliminar != null) { %>
  <p class="exito"><%= msgEliminar %></p>
  <% } %>
</div>

<!-- Botón "Añadir Usuario" -->
<div class="boton-anadir-contenedor">
  <button type="button" id="btnShowCreate" class="btn btn-anadir" onclick="showCreateForm()">Añadir Usuario</button>
</div>

<!-- Formulario de creación (inicialmente oculto) -->
<div id="formCrear" class="form-crear oculto">
  <h3>Crear Nuevo Usuario</h3>
  <form action="usuarios" method="post" class="form-inner">
    <input type="hidden" name="action" value="crear" />

    <label for="nuevoUsuario">Usuario:</label>
    <input type="text" name="usuario" id="nuevoUsuario" />

    <label for="nuevaContrasena">Contraseña:</label>
    <input type="password" name="contrasena" id="nuevaContrasena" />

    <div class="botones-form">
      <button type="submit" class="btn btn-crear">Guardar</button>
      <button type="button" class="btn btn-cancelar" onclick="hideCreateForm()">Cancelar</button>
    </div>
  </form>
</div>

<!-- Tabla de usuarios existentes -->
<div class="tabla-contenedor">
  <table>
    <thead>
    <tr>
      <th>ID</th>
      <th>Usuario</th>

      <!-- <th>Contraseña</th> -->

      <th>Acciones</th>
    </tr>
    </thead>
    <tbody>
    <%
      if (usuarios != null && !usuarios.isEmpty()) {
        for (Usuario u : usuarios) {
    %>
    <tr>
      <td><%= u.getId() %></td>
      <td><%= u.getUsuario() %></td>


      <!--<td><%= u.getContrasena() %></td>-->


      <td>
        <!-- Botón Editar -->
        <button
                type="button"
                class="btn btn-editar"
                onclick="abrirEditarModal(
                  <%= u.getId() %>,
                        '<%= u.getUsuario().replace("'", "\\'") %>',
                        '<%= u.getContrasena().replace("'", "\\'") %>'
                        )">
          Editar
        </button>

        <!-- Botón Eliminar -->
        <button
                type="button"
                class="btn btn-eliminar"
                onclick="abrirEliminarModal(<%= u.getId() %>)">
          Eliminar
        </button>
      </td>
    </tr>
    <%
      }
    } else {
    %>
    <tr>
      <td colspan="4" style="text-align:center; padding: 12px;">No hay usuarios registrados.</td>
    </tr>
    <%
      }
    %>
    </tbody>
  </table>
</div>

<!-- Modal para editar usuario -->
<div id="modalEditarUsuario" class="modal oculto">
  <div class="modal-contenido">
    <h3>Editar Usuario</h3>
    <form id="formEditar" action="usuarios" method="post" class="form-inner">
      <input type="hidden" name="action" value="editar" />
      <input type="hidden" name="id" id="editId" />

      <label for="editUsuario">Usuario:</label>
      <input type="text" name="usuario" id="editUsuario" />

      <label for="editContrasena">Contraseña:</label>
      <input type="password" name="contrasena" id="editContrasena" />

      <div class="botones-form">
        <button type="submit" class="btn btn-guardar">Guardar</button>
        <button type="button" class="btn btn-cancelar" onclick="cerrarEditarModal()">Cancelar</button>
      </div>
    </form>
  </div>
</div>

<!-- Modal para eliminar usuario -->
<div id="modalEliminarUsuario" class="modal oculto">
  <div class="modal-contenido">
    <h3>Confirmar Eliminación</h3>
    <p>¿Estás seguro de que deseas eliminar este usuario?</p>
    <form id="formEliminar" action="usuarios" method="post" class="form-inner">
      <input type="hidden" name="action" value="eliminar" />
      <input type="hidden" name="id" id="deleteId" />
      <div class="botones-form">
        <button type="submit" class="btn btn-eliminar">Sí, eliminar</button>
        <button type="button" class="btn btn-cancelar" onclick="cerrarEliminarModal()">Cancelar</button>
      </div>
    </form>
  </div>
</div>

<script src="js/usuarios.js"></script>
</body>
</html>
