<%-- ========================================================= --%>
<%-- SECCIÓN: Directivas JSP y carga de clases necesarias --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" %> <%-- Define que el lenguaje es Java y el tipo de contenido es HTML con codificación UTF-8 --%>
<%@ page import="java.util.List, model.Producto" %> <%-- Importa las clases necesarias: List y el modelo Producto --%>
<%-- Si se modifica el contentType o se omite el import de Producto, no se podrán mostrar los productos ni renderizar correctamente el HTML --%>
<%-- ========================================================= --%>

<!DOCTYPE html> <%-- Define el tipo de documento HTML5 --%>
<html>
<head>
  <meta charset="UTF-8"> <%-- Codificación UTF-8 para caracteres especiales --%>
  <title>Productos - Mimir Petshop</title> <%-- Título que aparece en la pestaña del navegador --%>

  <%-- Fuente de Google Fonts para estilos de texto --%>
  <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600&display=swap" rel="stylesheet">

  <%-- Hoja de estilos personalizada para esta página --%>
  <link rel="stylesheet" href="css/productos.css" />
</head>
<body>

<%-- ========================================================= --%>
<%-- SECCIÓN: Recuperación de datos desde la sesión y el request --%>
<%
  List<Producto> productos = (List<Producto>) request.getAttribute("productos"); // Obtiene la lista de productos desde el request
  String rol     = (String) session.getAttribute("rol");     // Obtiene el rol del usuario desde la sesión
  String usuario = (String) session.getAttribute("usuario"); // Obtiene el nombre del usuario autenticado desde la sesión
  String error   = request.getParameter("error");            // Captura errores enviados por parámetros GET (?error=...)

  // Si no hay usuario en sesión, redirige al login (seguridad básica)
  if (usuario == null) {
    response.sendRedirect("login"); // Redirige si el usuario no está autenticado
    return; // Detiene la ejecución del resto del JSP
  }
%>
<%-- Si se modifica este bloque, se podría permitir acceso no autorizado a usuarios no autenticados --%>
<%-- ========================================================= --%>

<%-- SECCIÓN: Barra superior con saludo y opciones comunes --%>
<div class="top-bar">
  <div>
    Bienvenido, <strong><%= usuario %></strong> (<%= rol %>) <%-- Muestra el nombre del usuario y su rol --%>
  </div>
  <div>
    <a href="cart"><button class="btn">Ver Carrito</button></a> <%-- Botón para ver el carrito de compras --%>
    <a href="logout"><button class="btn">Cerrar Sesión</button></a> <%-- Botón para cerrar sesión --%>
  </div>
</div>

<%-- ========================================================= --%>
<%-- SECCIÓN: Barra de acciones administrativas (visible solo a ADMIN) --%>
<% if ("ADMIN".equals(rol)) { %> <%-- Si el rol es ADMIN, se muestran estas acciones --%>
<div class="action-bar">
  <a href="addProduct"><button class="btn btn-add">+ Añadir Producto</button></a> <%-- Acceso a formulario para añadir un nuevo producto --%>
  <a href="productosInactivos"><button class="btn btn-inactivos">Ver Inactivos</button></a> <%-- Enlace para ver productos desactivados --%>
  <a href="usuarios"><button class="btn btn-admin-usuarios">Administrar Usuarios</button></a> <%-- Enlace para gestionar usuarios --%>
</div>
<% } %>
<%-- Si se elimina esta validación del rol, cualquier usuario podría acceder a acciones administrativas --%>
<%-- ========================================================= --%>

<%-- SECCIÓN: Mensajes de error o éxito al interactuar con productos --%>
<% if (error != null) { %> <%-- Si hay un error, muestra el mensaje correspondiente --%>
<p style="color: red; text-align: center;">
  <%=
  "missingParams".equals(error) ? "Faltan datos del formulario." : // Si faltan datos del formulario
          "invalidInput".equals(error) ? "Datos inválidos. Verifica la cantidad." : // Si la cantidad es inválida
                  "Ocurrió un error." // Mensaje genérico por defecto
  %>
</p>
<% } else if ("added".equals(request.getParameter("success"))) { %> <%-- Si se agregó correctamente el producto al carrito --%>
<p style="color: green; text-align: center;">
  Producto añadido al carrito correctamente.
</p>
<% } %>

<%-- ========================================================= --%>
<%-- SECCIÓN: Tabla de productos disponibles --%>
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
  <%-- Itera sobre cada producto y lo muestra en una fila --%>
  <%
    if (productos != null) { // Verifica que la lista no sea nula
      for (Producto prod : productos) { // Itera sobre la lista de productos
  %>
  <tr>
    <td><%= prod.getCategoria().getNombre() %></td> <%-- Muestra la categoría del producto --%>
    <td><%= prod.getNombre() %></td> <%-- Muestra el nombre del producto --%>
    <td><%= prod.getMarca() %></td> <%-- Muestra la marca --%>
    <td>$ <%= prod.getPrecio() %></td> <%-- Muestra el precio --%>
    <td><%= prod.getStock() %></td> <%-- Muestra el stock disponible --%>
    <td>
      <%-- FORMULARIO: Comprar producto (cantidad configurable) --%>
      <form action="addToCart" method="post" style="display:inline-block;" onsubmit="return validarCantidad(this);">
        <input type="hidden" name="productoId" value="<%= prod.getId() %>" /> <%-- Envia el ID del producto oculto --%>
        <input type="number" name="cantidad" value="1" min="1"
               data-stock="<%= prod.getStock() %>" style="width: 50px;" /> <%-- Campo de cantidad a comprar --%>
        <button type="submit" class="btn btn-comprar">Comprar</button> <%-- Botón de compra --%>
      </form>

      <%-- Acciones exclusivas para administradores --%>
      <% if ("ADMIN".equals(rol)) { %>
      <a href="editProduct?id=<%= prod.getId() %>">
        <button class="btn btn-editar">Editar</button> <%-- Botón para editar producto --%>
      </a>
      <button type="button" class="btn btn-eliminar" onclick="confirmarEliminacion(<%= prod.getId() %>)">
        Eliminar
      </button> <%-- Botón para abrir modal de confirmación de eliminación --%>
      <% } %>
    </td>
  </tr>
  <%
      } // Fin del for
    } // Fin del if
  %>
  </tbody>
</table>
<%-- Si se elimina este bloque de control de rol o la validación JS, se podrían procesar compras inválidas o sin control de stock --%>

<%-- ========================================================= --%>
<%-- MODAL: Confirmación de eliminación (solo admins) --%>
<div id="modalEliminar">
  <div class="modal-content">
    <h3>¿Estás seguro?</h3> <%-- Pregunta de confirmación --%>
    <p>Esta acción desactivará el producto.</p>
    <div style="margin-top: 20px;">
      <button onclick="eliminarProducto()" style="background-color:#f44336; color:white; padding:8px 16px; border:none; border-radius:4px;">Eliminar</button>
      <button onclick="cerrarModal()" style="margin-left:10px; padding:8px 16px; border:none; border-radius:4px;">Cancelar</button>
    </div>
  </div>
</div>

<%-- ========================================================= --%>
<%-- MODAL: Error al superar el stock disponible --%>
<div id="modalStockError">
  <div>
    <h3>No puedes superar la cantidad que tenemos disponible</h3> <%-- Mensaje cuando se intenta comprar más del stock --%>
    <button onclick="cerrarModalStock()">Cerrar</button> <%-- Cierra el modal --%>
  </div>
</div>

<%-- ========================================================= --%>
<%-- SCRIPTS: JavaScript para validaciones y funcionalidad dinámica --%>
<script src="js/productos.js"></script> <%-- Carga el archivo JS que gestiona validaciones, modales, etc. --%>
<%-- Si se modifica este script sin cuidado, se romperá la validación de stock, la confirmación de eliminación o la experiencia de compra --%>
</body>
</html>
