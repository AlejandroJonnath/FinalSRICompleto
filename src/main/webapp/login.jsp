<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8" />
  <title>Login y Registro - Mimir Petshop</title>
  <link rel="stylesheet" href="<%=request.getContextPath()%>/css/login.css">
</head>
<body>
<div class="login-container">

  <h2>Iniciar Sesión</h2>

  <%
    String error = (String) request.getAttribute("error");
    if (error != null) {
  %>
  <div class="error"><%= error %></div>
  <% } %>

  <form action="login" method="post" style="display: inline-block;">
    <input type="hidden" name="action" value="login" />
    <label for="usuario">Usuario</label><br/>
    <%
      String usuarioPrevio = (String) request.getAttribute("usuario");
      if (usuarioPrevio == null) {
        usuarioPrevio = "";
      }
    %>
    <input type="text" name="usuario" id="usuario" value="<%= usuarioPrevio %>"/><br/>

    <label for="contrasena">Contraseña</label><br/>
    <input type="password" name="contrasena" id="contrasena" /><br/><br/>

    <button type="submit">Entrar</button>
  </form>

  <!-- Botón pequeño para abrir modal -->
  <button class="btn-small" id="openRegisterBtn">Registrarse</button>

</div>

<!-- Modal de Registro -->
<div id="registerModal" class="modal">
  <div class="modal-content">
    <span class="close-btn" id="closeRegisterBtn">&times;</span>
    <h2>Registrar Usuario</h2>

    <%
      String errorRegister = (String) request.getAttribute("errorRegister");
      String msgSuccess = (String) request.getAttribute("msgSuccess");
      if (errorRegister != null) {
    %>
    <div class="error"><%= errorRegister %></div>
    <% } else if (msgSuccess != null) { %>
    <div class="success"><%= msgSuccess %></div>
    <% } %>

    <form action="login" method="post">
      <input type="hidden" name="action" value="register" />
      <label for="usuarioRegister">Usuario</label><br/>
      <input type="text" name="usuario" id="usuarioRegister"/><br/>

      <label for="contrasenaRegister">Contraseña</label><br/>
      <input type="password" name="contrasena" id="contrasenaRegister"/><br/><br/>

      <button type="submit">Registrar</button>
    </form>
  </div>
</div>

<script>
  const openBtn = document.getElementById('openRegisterBtn');
  const modal = document.getElementById('registerModal');
  const closeBtn = document.getElementById('closeRegisterBtn');

  window.onload = function() {
    const modal = document.getElementById('registerModal');
    modal.style.display = 'none'; // oculta modal al cargar la página
  };

  openBtn.onclick = function() {
    modal.style.display = 'block';
  };

  closeBtn.onclick = function() {
    modal.style.display = 'none';
  };

  // Cerrar modal si clic fuera del contenido
  window.onclick = function(event) {
    if (event.target == modal) {
      modal.style.display = 'none';
    }
  };
</script>
</body>
</html>
