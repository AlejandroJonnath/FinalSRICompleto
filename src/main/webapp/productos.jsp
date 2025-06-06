<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List, model.Producto" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Productos - Mimir Petshop</title>
  <link rel="stylesheet" href="css/productos.css" />
</head>
<body>
<%
  List<Producto> productos = (List<Producto>) request.getAttribute("productos");
  String rol     = (String) session.getAttribute("rol");
  String usuario = (String) session.getAttribute("usuario");
  String error   = request.getParameter("error");

  // Si no está autenticado, redirigimos al login
  if (usuario == null) {
    response.sendRedirect("login");
    return;
  }
%>

<div class="top-bar">
  <div>
    Bienvenido, <strong><%= usuario %></strong> (<%= rol %>)
  </div>
  <div>
    <a href="cart"><button class="btn">Ver Carrito</button></a>
    <a href="logout"><button class="btn">Cerrar Sesión</button></a>
  </div>
</div>

<% if ("ADMIN".equals(rol)) { %>
<div class="action-bar">
  <a href="addProduct"><button class="btn btn-add">+ Añadir Producto</button></a>
  <a href="productosInactivos"><button class="btn btn-inactivos">Ver Inactivos</button></a>
  <!-- NUEVO BOTÓN: Solo para ADMIN -->
  <a href="usuarios"><button class="btn btn-admin-usuarios">Administrar Usuarios</button></a>
</div>
</div>
<% } %>

<% if (error != null) { %>
<p style="color: red; text-align: center;">
  <%=
  "missingParams".equals(error) ? "Faltan datos del formulario." :
          "invalidInput".equals(error) ? "Datos inválidos. Verifica la cantidad." :
                  "Ocurrió un error."
  %>
</p>
<% } else if ("added".equals(request.getParameter("success"))) { %>
<p style="color: green; text-align: center;">
  Producto añadido al carrito correctamente.
</p>
<% } %>

<table>
  <thead>
  <tr>
    <th>Categoria</th>
    <th>Nombre</th>
    <th>Marca</th>
    <th>Precio</th>
    <th>Stock</th>
    <th>Acciones</th>
  </tr>
  </thead>
  <tbody>
  <%
    if (productos != null) {
      for (Producto prod : productos) {

  %>
  <tr>
    <td><%= prod.getCategoria().getNombre() %></td>
    <td><%= prod.getNombre() %></td>
    <td><%= prod.getMarca() %></td>
    <td>$ <%= prod.getPrecio() %></td>
    <td><%= prod.getStock() %></td>
    <td>
      <form action="addToCart" method="post" style="display:inline-block;" onsubmit="return validarCantidad(this);">
        <input type="hidden" name="productoId" value="<%= prod.getId() %>" />
        <input type="number" name="cantidad" value="1" min="1" data-stock="<%= prod.getStock() %>" style="width: 50px;" />
        <button type="submit" class="btn btn-comprar">Comprar</button>
      </form>
      <% if ("ADMIN".equals(rol)) { %>
      <a href="editProduct?id=<%= prod.getId() %>">
        <button class="btn btn-editar">Editar</button>
      </a>
      <button type="button" class="btn btn-eliminar" onclick="confirmarEliminacion(<%= prod.getId() %>)">
        Eliminar
      </button>
      <% } %>
    </td>
  </tr>
  <%
      }
    }
  %>
  </tbody>
</table>

<!-- Modal para eliminar producto -->
<div id="modalEliminar">
  <div class="modal-content">
    <h3>¿Estás seguro?</h3>
    <p>Esta acción desactivará el producto.</p>
    <div style="margin-top: 20px;">
      <button onclick="eliminarProducto()" style="background-color:#f44336; color:white; padding:8px 16px; border:none; border-radius:4px;">Eliminar</button>
      <button onclick="cerrarModal()" style="margin-left:10px; padding:8px 16px; border:none; border-radius:4px;">Cancelar</button>
    </div>
  </div>
</div>

<!-- Modal para error de stock -->
<div id="modalStockError">
  <div>
    <h3>No puedes superar la cantidad que tenemos disponible</h3>
    <button onclick="cerrarModalStock()">Cerrar</button>
  </div>
</div>

<script src="js/productos.js"></script>
</body>
</html>
