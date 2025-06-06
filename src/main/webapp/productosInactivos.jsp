<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List, model.Producto" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Productos Inactivos - Mimir Petshop</title>
  <link rel="stylesheet" href="css/Inactivos.css">
</head>
<body>
<%
  String rol = (String) session.getAttribute("rol");
  String usuario = (String) session.getAttribute("usuario");
  if (usuario == null || !"ADMIN".equals(rol)) {
    response.sendRedirect("productos");
    return;
  }

  List<Producto> inactivos = (List<Producto>) request.getAttribute("productosInactivos");
%>

<div class="top-bar">
  <div>
    <a href="productos"><button class="btn btn-volver">← Volver a Productos Activos</button></a>
  </div>
  <div>
    Bienvenido, <strong><%= usuario %></strong> (<%= rol %>)
  </div>
</div>

<h2 style="text-align: center; margin-top: 20px;">Productos Inactivos</h2>

<table>
  <thead>
  <tr>
    <th>Categoria</th>
    <th>Nombre</th>
    <th>Marca</th>
    <th>Precio</th>
    <th>Stock</th>
    <th>Acción</th>
  </tr>
  </thead>
  <tbody>
  <%
    if (inactivos != null && !inactivos.isEmpty()) {
      for (Producto p : inactivos) {
  %>
  <tr>
    <td><%= p.getCategoria().getNombre() %></td>
    <td><%= p.getNombre() %></td>
    <td><%= p.getMarca() %></td>
    <td>$ <%= p.getPrecio() %></td>
    <td><%= p.getStock() %></td>
    <td>
      <button class="btn btn-reactivar" onclick="confirmarReactivacion(<%= p.getId() %>)">
        Reactivar
      </button>
    </td>
  </tr>
  <%
    }
  } else {
  %>
  <tr>
    <td colspan="5">No hay productos inactivos.</td>
  </tr>
  <% } %>
  </tbody>
</table>

<!-- Modal Reactivar -->
<div id="modalReactivar">
  <div class="modal-content">
    <h3>¿Reactivar producto?</h3>
    <p>El producto volverá a estar disponible para la venta.</p>
    <div style="margin-top: 20px;">
      <button onclick="reactivarProducto()" class="btn btn-reactivar">Reactivar</button>
      <button onclick="cerrarModal()" class="btn" style="margin-left:10px;">Cancelar</button>
    </div>
  </div>
</div>

<script src="js/inactivos.js"></script>
</body>
</html>
