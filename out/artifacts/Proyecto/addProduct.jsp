<%-- ======================================================== --%>
<%-- DIRECTIVAS DE PÁGINA: Importaciones y configuración general --%>
<%@ page import="model.Categoria" %>                    <%-- Importa la clase Categoria del paquete model para poder usarla en este JSP --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%-- Define que el lenguaje es Java y establece UTF-8 como codificación --%>
<%@ page import="java.util.List" %>                     <%-- Importa la interfaz List de java.util para manejar listas --%>
<%-- ======================================================== --%>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8"><!-- Define la codificación de caracteres para el navegador -->
  <title>Añadir Producto - Mimir Petshop</title><!-- Título de la página -->
  <link rel="stylesheet" href="css/addProduct.css"><!-- Enlace a la hoja de estilos externa -->

  <style>
    /* Estilos inline para mostrar errores */
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
  // Recuperar el rol del usuario desde la sesión
  String rol = (String) session.getAttribute("rol");
  // Si no hay rol o no es ADMIN, redirigir a la lista de productos
  if (rol == null || !"ADMIN".equals(rol)) {
    response.sendRedirect("productos");
    return; // Interrumpe la ejecución del JSP
  }

  // Recuperar posibles errores y listas de categorías de los atributos de la solicitud
  @SuppressWarnings("unchecked")
  List<String> errores = (List<String>) request.getAttribute("errores");
  @SuppressWarnings("unchecked")
  List<Categoria> categorias = (List<Categoria>) request.getAttribute("categorias");

  // Variables para mantener valores previos en caso de error y repoblar el formulario
  String categoriaPrev = request.getParameter("categoriaId") != null
          ? request.getParameter("categoriaId") : "";
  String nombrePrev    = request.getAttribute("nombre") != null
          ? (String) request.getAttribute("nombre") : "";
  String marcaPrev     = request.getAttribute("marca") != null
          ? (String) request.getAttribute("marca") : "";
  String precioPrev    = request.getAttribute("precio") != null
          ? (String) request.getAttribute("precio") : "";
  String stockPrev     = request.getAttribute("stock") != null
          ? (String) request.getAttribute("stock") : "";
%>
<%-- FIN BLOQUE CONTROL Y DATOS --%>

<div class="form-container">
  <a href="productos" class="top-link">← Regresar a Productos</a>  <%-- Enlace para volver atrás --%>
  <h2>Añadir Nuevo Producto</h2>                                 <%-- Encabezado del formulario --%>

  <form action="addProduct" method="post"><!-- Formulario que envía a la URL /addProduct vía POST -->

    <%-- ======================================================== --%>
    <%-- SECCIÓN: Selección de CATEGORÍA --%>
    <label for="categoriaId">Categoría:</label>
    <select name="categoriaId" id="categoriaId">
      <option value="">-- Seleccionar categoría --</option>
      <% if (categorias != null) {                         // Si la lista de categorías no es null
        for (Categoria cat : categorias) {              // Itera cada objeto Categoria
          boolean selected = String.valueOf(cat.getId()).equals(categoriaPrev); // Marca la opción seleccionada
      %>
      <option value="<%= cat.getId() %>" <%= selected ? "selected" : "" %>>
        <%= cat.getNombre() %>                            <!-- Muestra el nombre de la categoría -->
      </option>
      <%   }
      } %>
    </select>
    <%-- Mostrar errores específicos de categoría --%>
    <% if (errores != null) {
      for (String err : errores) {
        if (err.toLowerCase().contains("categoría") || err.toLowerCase().contains("categoria")) { %>
    <span class="error-field"><%= err %></span>
    <%     }
    }
    } %>
    <%-- FIN SECCIÓN CATEGORÍA: Permite elegir categoría y muestra posibles errores. --%>

    <%-- ======================================================== --%>
    <%-- SECCIÓN: Campo NOMBRE --%>
    <label for="nombre">Nombre:</label>
    <input type="text" name="nombre" id="nombre" value="<%= nombrePrev %>" />
    <% if (errores != null) {
      for (String err : errores) {
        if (err.toLowerCase().contains("nombre")) { %>
    <span class="error-field"><%= err %></span>
    <%     }
    }
    } %>
    <%-- FIN SECCIÓN NOMBRE: Captura y valida el nombre del producto. --%>

    <%-- ======================================================== --%>
    <%-- SECCIÓN: Campo MARCA --%>
    <label for="marca">Marca:</label>
    <input type="text" name="marca" id="marca" value="<%= marcaPrev %>" />
    <% if (errores != null) {
      for (String err : errores) {
        if (err.toLowerCase().contains("marca")) { %>
    <span class="error-field"><%= err %></span>
    <%     }
    }
    } %>
    <%-- FIN SECCIÓN MARCA: Captura y valida la marca del producto. --%>

    <%-- ======================================================== --%>
    <%-- SECCIÓN: Campo PRECIO --%>
    <label for="precio">Precio:</label>
    <input type="text" name="precio" id="precio" value="<%= precioPrev %>" />
    <% if (errores != null) {
      for (String err : errores) {
        if (err.toLowerCase().contains("precio")) { %>
    <span class="error-field"><%= err %></span>
    <%     }
    }
    } %>
    <%-- FIN SECCIÓN PRECIO: Captura y valida el precio del producto. --%>

    <%-- ======================================================== --%>
    <%-- SECCIÓN: Campo STOCK --%>
    <label for="stock">Stock:</label>
    <input type="text" name="stock" id="stock" value="<%= stockPrev %>" />
    <% if (errores != null) {
      for (String err : errores) {
        if (err.toLowerCase().contains("stock")) { %>
    <span class="error-field"><%= err %></span>
    <%     }
    }
    } %>
    <%-- FIN SECCIÓN STOCK: Captura y valida la cantidad en inventario. --%>

    <button type="submit">Guardar Producto</button>  <%-- Botón para enviar el formulario --%>

    <%-- ======================================================== --%>
    <%-- SECCIÓN: Errores GENERALES que no corresponden a un campo específico --%>
    <% if (errores != null) {
      for (String err : errores) {
        String low = err.toLowerCase();
        boolean esCampo = low.contains("nombre") || low.contains("marca") ||
                low.contains("precio") || low.contains("stock") ||
                low.contains("categoria") || low.contains("categoría");
        if (!esCampo) { %>
    <span class="error-general"><%= err %></span>
    <%       }
    }
    } %>
    <%-- FIN SECCIÓN ERRORES GENERALES: Muestra mensajes globales como "Fallo al guardar". --%>

  </form>
</div>

</body>
</html>
