<%@ page import="model.Categoria" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Añadir Producto - Mimir Petshop</title>
  <link rel="stylesheet" href="css/addProduct.css">
  <style>
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
<%
  String rol = (String) session.getAttribute("rol");
  if (rol == null || !"ADMIN".equals(rol)) {
    response.sendRedirect("productos");
    return;
  }

  @SuppressWarnings("unchecked")
  List<String> errores = (List<String>) request.getAttribute("errores");
  @SuppressWarnings("unchecked")
  List<Categoria> categorias = (List<Categoria>) request.getAttribute("categorias");

  String categoriaPrev = request.getParameter("categoriaId") != null ? request.getParameter("categoriaId") : "";
  String nombrePrev    = request.getAttribute("nombre") != null ? (String) request.getAttribute("nombre") : "";
  String marcaPrev     = request.getAttribute("marca")  != null ? (String) request.getAttribute("marca")  : "";
  String precioPrev    = request.getAttribute("precio") != null ? (String) request.getAttribute("precio") : "";
  String stockPrev     = request.getAttribute("stock")  != null ? (String) request.getAttribute("stock")  : "";
%>

<div class="form-container">
  <a href="productos" class="top-link">← Regresar a Productos</a>
  <h2>Añadir Nuevo Producto</h2>

  <form action="addProduct" method="post">

    <!-- Campo CATEGORÍA -->
    <label for="categoriaId">Categoría:</label>
    <select name="categoriaId" id="categoriaId">
      <option value="">-- Seleccionar categoría --</option>
      <% if (categorias != null) {
        for (Categoria cat : categorias) {
          boolean selected = String.valueOf(cat.getId()).equals(categoriaPrev);
      %>
      <option value="<%= cat.getId() %>" <%= selected ? "selected" : "" %>><%= cat.getNombre() %></option>
      <% } } %>
    </select>
    <% if (errores != null) {
      for (String err : errores) {
        if (err.toLowerCase().contains("categoría") || err.toLowerCase().contains("categoria")) { %>
    <span class="error-field"><%= err %></span>
    <%     }
    }
    } %>

    <!-- Campo NOMBRE -->
    <label for="nombre">Nombre:</label>
    <input type="text" name="nombre" id="nombre" value="<%= nombrePrev %>" />
    <% if (errores != null) {
      for (String err : errores) {
        if (err.toLowerCase().contains("nombre")) { %>
    <span class="error-field"><%= err %></span>
    <% } } } %>

    <!-- Campo MARCA -->
    <label for="marca">Marca:</label>
    <input type="text" name="marca" id="marca" value="<%= marcaPrev %>" />
    <% if (errores != null) {
      for (String err : errores) {
        if (err.toLowerCase().contains("marca")) { %>
    <span class="error-field"><%= err %></span>
    <% } } } %>

    <!-- Campo PRECIO -->
    <label for="precio">Precio:</label>
    <input type="text" name="precio" id="precio" value="<%= precioPrev %>" />
    <% if (errores != null) {
      for (String err : errores) {
        if (err.toLowerCase().contains("precio")) { %>
    <span class="error-field"><%= err %></span>
    <% } } } %>

    <!-- Campo STOCK -->
    <label for="stock">Stock:</label>
    <input type="text" name="stock" id="stock" value="<%= stockPrev %>" />
    <% if (errores != null) {
      for (String err : errores) {
        if (err.toLowerCase().contains("stock")) { %>
    <span class="error-field"><%= err %></span>
    <% } } } %>

    <button type="submit">Guardar Producto</button>

    <!-- Errores generales -->
    <% if (errores != null) {
      for (String err : errores) {
        String low = err.toLowerCase();
        boolean esCampo = low.contains("nombre") || low.contains("marca") ||
                low.contains("precio") || low.contains("stock") ||
                low.contains("categoria") || low.contains("categoría");
        if (!esCampo) { %>
    <span class="error-general"><%= err %></span>
    <% } } } %>
  </form>
</div>
</body>
</html>
