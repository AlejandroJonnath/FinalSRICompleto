<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Compra Exitosa - Mimir Petshop</title>
  <link rel="stylesheet" href="css/compraexitosa.css">
</head>
<body>
<%
  String mensajeExito = (String) request.getAttribute("mensajeExito");
  Integer invoiceId = (Integer) request.getAttribute("invoiceId");
  if (mensajeExito == null || invoiceId == null) {
    response.sendRedirect("productos");
    return;
  }
%>
<div class="message">
  <h2>¡Compra Realizada Con Éxito!</h2>
  <p><%= mensajeExito %></p>

  <a href="productos"><button class="btn">Volver a Productos</button></a>
  <a href="downloadInvoice?invoiceId=<%= invoiceId %>">
    <button class="btn">Descargar Factura</button>
  </a>
</div>
</body>
</html>