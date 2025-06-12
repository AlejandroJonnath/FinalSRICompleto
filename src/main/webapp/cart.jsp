<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.math.BigDecimal, java.util.List, java.util.Map, model.Producto" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Carrito de Compras - Mimir Petshop</title>
    <link rel="stylesheet" href="css/cart.css">
</head>
<body>
<%
    /* ---------- Lógica de sesión y datos ---------- */
    if (session.getAttribute("usuario") == null) {
        response.sendRedirect("login");
        return;
    }

    List<Map<String, Object>> items =
            (List<Map<String, Object>>) request.getAttribute("items");
    BigDecimal totalGeneral = (BigDecimal) request.getAttribute("totalGeneral");
    BigDecimal totalIva = (BigDecimal) request.getAttribute("totalIva");
    BigDecimal totalConIva = (BigDecimal) request.getAttribute("totalConIva");

    String mensajeError  = (String) request.getAttribute("mensajeError");
    String mensajeExito  = (String) request.getAttribute("mensajeExito");
%>

<div class="top-bar">
    <div>
        <a href="productos"><button class="btn">← Seguir Comprando</button></a>
    </div>
    <div>
        <a href="logout"><button class="btn">Cerrar Sesión</button></a>
    </div>
</div>

<% if (mensajeError != null) { %>
<div class="msg-error"><%= mensajeError %></div>
<% } %>
<% if (mensajeExito != null) { %>
<div class="msg-success"><%= mensajeExito %></div>
<% } %>

<table>
    <thead>
    <tr>
        <th>Nombre</th>
        <th>Precio Unitario</th>
        <th>Cantidad</th>
        <th>Subtotal</th>
        <th>Iva</th>
    </tr>
    </thead>
    <tbody>
    <%
        if (items != null) {
            for (Map<String, Object> item : items) {
                Producto p = (Producto) item.get("producto");
                Integer cantidad = (Integer) item.get("cantidad");
                BigDecimal subtotal = (BigDecimal) item.get("subtotal");
                BigDecimal iva = (BigDecimal) item.get("iva");

    %>
    <tr>
        <td><%= p.getNombre() %></td>
        <td>$ <%= p.getPrecio() %></td>
        <td><%= cantidad %></td>
        <td>$ <%= subtotal %></td>
        <td>$ <%= String.format("%.2f", iva) %></td>
    </tr>
    <%
            }
        }
    %>
    </tbody>
    <tfoot>
    <tr>
        <th colspan="4" style="text-align: right;">Total sin IVA</th>
        <th>$ <%= String.format("%.2f", (totalGeneral != null ? totalGeneral : BigDecimal.ZERO)) %></th>
    </tr>
    <tr>
        <th colspan="4" style="text-align: right;">Total IVA</th>
        <th>$ <%= String.format("%.2f", (totalIva != null ? totalIva : BigDecimal.ZERO)) %></th>
    </tr>
    <tr>
        <th colspan="4" style="text-align: right;">TOTAL A PAGAR</th>
        <th>$ <%= String.format("%.2f", (totalConIva != null ? totalConIva : BigDecimal.ZERO)) %></th>
    </tr>
    </tfoot>
</table>

<div class="form-checkout">
    <h3>Confirmar Compra</h3>
    <form action="checkout" method="post">
        <label for="clienteNombre">Nombre del Cliente:</label><br/>
        <input type="text" name="clienteNombre" id="clienteNombre" required /><br/>

        <label for="clienteCedula">Cédula:</label><br/>
        <input type="text" name="clienteCedula" id="clienteCedula" required /><br/>

        <label for="clienteDirección">Dirección:</label><br/>
        <input type="text" name="clienteDireccion" id="clienteDireccion" required /><br/>

        <label for="clienteTelefono">Telefono:</label><br/>
        <input type="text" name="clienteTelefono" id="clienteTelefono" required /><br/>

        <label for="clienteCorreo">Correo:</label><br/>
        <input type="text" name="clienteEmail" id="clienteEmail" required /><br/>

        <button type="submit" class="btn btn-checkout">Realizar Pago</button>
    </form>
</div>
</body>
</html>
