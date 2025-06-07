<%-- ======================================================== --%>
<%-- DIRECTIVAS DE PÁGINA: Configuración y importaciones --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%-- Define Java como lenguaje y UTF-8 como codificación de salida --%>
<%@ page import="model.Producto, model.Categoria, java.util.List" %>
<%-- Importa las clases Producto, Categoria y la interfaz List --%>
<%-- ======================================================== --%>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8"><!-- Codificación de caracteres para el navegador -->
  <title>Editar Producto - Mimir Petshop</title><!-- Título de la pestaña del navegador -->
  <link rel="stylesheet" href="css/editproduct.css"><!-- Hoja de estilos externa -->
  <style>
    /* Estilos inline para mostrar mensajes de error */
    .error-field {
      display: block;
      color: #c0392b;
      font-size: 0.9em;
      margin-top: 4px;
      margin-bottom: 12px;
    }
    .error-general {
      display: block;
      color: #c0392b;
      font-size: 1em;
      margin-top: 12px;
      margin-bottom: 12px;
      text-align: center;
    }
  </style>
</head>
<body>
<%-- ======================================================== --%>
<%-- BLOQUE DE CONTROL DE ACCESO Y PREPARACIÓN DE DATOS --%>
<%
  // 1. Verifica rol de usuario
  String rol = (String) session.getAttribute("rol");
  if (rol == null || !"ADMIN".equals(rol)) {
    // Si no es ADMIN, redirige a la lista de productos y detiene ejecución
    response.sendRedirect("productos");
    return;
  }

  // 2. Recupera lista de errores y categorías de la petición
  @SuppressWarnings("unchecked")
  List<String> errores = (List<String>) request.getAttribute("errores");
  List<Categoria> categorias = (List<Categoria>) request.getAttribute("categorias");

  // 3. Recupera valores previos (en caso de validación fallida) o null
  String idPrev     = request.getAttribute("id")     != null ? request.getAttribute("id").toString() : null;
  String nombrePrev = (String) request.getAttribute("nombre");
  String marcaPrev  = (String) request.getAttribute("marca");
  String precioPrev = (String) request.getAttribute("precio");
  String stockPrev  = (String) request.getAttribute("stock");

  // 4. Recupera el objeto Producto original (para datos actuales)
  Producto producto = (Producto) request.getAttribute("producto");

  // 5. Si no hay producto ni id previo, redirige fuera
  if (producto == null && idPrev == null) {
    response.sendRedirect("productos");
    return;
  }

  // 6. Determina el valor final de cada campo: previo si existe, si no la propiedad del producto
  String idValue     = idPrev     != null ? idPrev     : String.valueOf(producto.getId());
  String nombreValue = nombrePrev != null ? nombrePrev : producto.getNombre();
  String marcaValue  = marcaPrev  != null ? marcaPrev  : producto.getMarca();
  String precioValue = precioPrev != null ? precioPrev : producto.getPrecio().toString();
  String stockValue  = stockPrev  != null ? stockPrev  : String.valueOf(producto.getStock());
%>
<%-- FIN BLOQUE DE CONTROL Y DATOS --%>

<div class="form-container">
  <a href="productos" class="top-link">← Regresar a Productos</a>  <%-- Enlace para volver al listado --%>
  <h2>Editar Producto</h2>                                   <%-- Encabezado del formulario --%>

  <form action="editProduct" method="post"><!-- Envía datos a la ruta editProduct con método POST -->
    <input type="hidden" name="id" value="<%= idValue %>" /><!-- ID oculto para identificar el producto -->

    <%-- ======================================================== --%>
    <%-- SECCIÓN: Selección de CATEGORÍA --%>
    <label for="categoriaId">Categoría:</label>
    <select name="categoriaId" id="categoriaId">
      <option value="">-- Seleccionar categoría --</option>
      <% if (categorias != null) {
        for (Categoria cat : categorias) {
          // Marca la opción si coincide con la categoría actual del producto
          boolean selected = producto.getCategoria() != null
                  && cat.getId() == producto.getCategoria().getId();
      %>
      <option value="<%= cat.getId() %>" <%= selected ? "selected" : "" %>>
        <%= cat.getNombre() %>                <!-- Muestra el nombre de la categoría -->
      </option>
      <%   }
      } %>
    </select>
    <%-- No se muestran errores de categoría en edición; asume válida si existe producto. --%>
    <%-- FIN SECCIÓN CATEGORÍA: Permite reasignar categoría al producto. --%>

    <%-- ======================================================== --%>
    <%-- SECCIÓN: Campo NOMBRE --%>
    <label for="nombre">Nombre:</label>
    <input type="text" name="nombre" id="nombre" value="<%= nombreValue %>" />
    <% if (errores != null) {
      for (String err : errores) {
        if (err.toLowerCase().contains("nombre")) { %>
    <span class="error-field"><%= err %></span> <!-- Muestra mensaje de error para nombre -->
    <%       }
    }
    } %>
    <%-- FIN SECCIÓN NOMBRE: Edita y valida el nombre del producto. --%>

    <%-- ======================================================== --%>
    <%-- SECCIÓN: Campo MARCA --%>
    <label for="marca">Marca:</label>
    <input type="text" name="marca" id="marca" value="<%= marcaValue %>" />
    <% if (errores != null) {
      for (String err : errores) {
        if (err.toLowerCase().contains("marca")) { %>
    <span class="error-field"><%= err %></span> <!-- Muestra mensaje de error para marca -->
    <%       }
    }
    } %>
    <%-- FIN SECCIÓN MARCA: Edita y valida la marca del producto. --%>

    <%-- ======================================================== --%>
    <%-- SECCIÓN: Campo PRECIO --%>
    <label for="precio">Precio:</label>
    <input type="text" name="precio" id="precio" value="<%= precioValue %>" />
    <% if (errores != null) {
      for (String err : errores) {
        if (err.toLowerCase().contains("precio")) { %>
    <span class="error-field"><%= err %></span> <!-- Muestra mensaje de error para precio -->
    <%       }
    }
    } %>
    <%-- FIN SECCIÓN PRECIO: Edita y valida el precio del producto. --%>

    <%-- ======================================================== --%>
    <%-- SECCIÓN: Campo STOCK --%>
    <label for="stock">Stock:</label>
    <input type="text" name="stock" id="stock" value="<%= stockValue %>" />
    <% if (errores != null) {
      for (String err : errores) {
        if (err.toLowerCase().contains("stock")) { %>
    <span class="error-field"><%= err %></span> <!-- Muestra mensaje de error para stock -->
    <%       }
    }
    } %>
    <%-- FIN SECCIÓN STOCK: Edita y valida la cantidad en inventario. --%>

    <button type="submit">Actualizar Producto</button> <!-- Botón para enviar la edición -->

    <%-- ======================================================== --%>
    <%-- SECCIÓN: Errores GENERALES (no ligados a un campo específico) --%>
    <% if (errores != null) {
      for (String err : errores) {
        String low = err.toLowerCase();
        boolean esCampo = low.contains("nombre") || low.contains("marca")
                || low.contains("precio") || low.contains("stock");
        if (!esCampo) { %>
    <span class="error-general"><%= err %></span> <!-- Mensaje general de fallo al editar -->
    <%       }
    }
    } %>
    <%-- FIN SECCIÓN ERRORES GENERALES: Muestra errores globales de la operación. --%>

  </form>
</div>

</body>
</html>
