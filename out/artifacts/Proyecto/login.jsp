<%-- ======================================================== --%>
<%-- DIRECTIVA DE PÁGINA: configuración de JSP y codificación --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%-- Define Java como lenguaje y UTF-8 como charset --%>
<%-- ======================================================== --%>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8" /><!-- Codificación de caracteres para el navegador -->
  <title>Login y Registro - Mimir Petshop</title><!-- Título de la pestaña -->
  <link rel="stylesheet" href="<%=request.getContextPath()%>/css/login.css"><!-- Enlaza la hoja de estilos -->
</head>
<body>
<div class="login-container"><!-- Contenedor principal de la sección de login -->

  <h2>Iniciar Sesión</h2><!-- Título de la sección de login -->

  <%-- ======================================================== --%>
  <%-- BLOQUE: Mostrar error de login si existe --%>
  <%
    // Obtiene el atributo "error" que proviene de LoginServlet (mensaje de fallo)
    String error = (String) request.getAttribute("error");
    if (error != null) {  // Si hay mensaje de error, mostrarlo
  %>
  <div class="error"><%= error %></div><!-- Div que muestra el mensaje de error -->
  <% } %>
  <%-- FIN BLOQUE ERROR LOGIN --%>

  <%-- ======================================================== --%>
  <%-- FORMULARIO: Iniciar sesión --%>
  <form action="login" method="post" style="display: inline-block;">
    <input type="hidden" name="action" value="login" /><!-- Oculto para indicar acción login -->

    <label for="usuario">Usuario</label><br/> <!-- Etiqueta para el campo usuario -->
    <%
      // Recupera nombre de usuario previamente ingresado en caso de error
      String usuarioPrevio = (String) request.getAttribute("usuario");
      if (usuarioPrevio == null) {
        usuarioPrevio = "";  // Si es null, dejar vacío
      }
    %>
    <input type="text"
           name="usuario"
           id="usuario"
           value="<%= usuarioPrevio %>"/><br/> <!-- Input con valor previo -->

    <label for="contrasena">Contraseña</label><br/> <!-- Etiqueta para contraseña -->
    <input type="password" name="contrasena" id="contrasena" /><br/><br/> <!-- Campo contraseña -->

    <button type="submit">Entrar</button> <!-- Botón de envío -->
  </form>
  <%-- FIN FORMULARIO LOGIN --%>

  <!-- Botón pequeño para abrir modal de registro -->
  <button class="btn-small" id="openRegisterBtn">Registrarse</button>

</div> <!-- Fin .login-container -->

<!-- ======================================================== -->
<!-- MODAL DE REGISTRO: inicialmente oculto -->
<div id="registerModal" class="modal">
  <div class="modal-content"><!-- Contenido del modal -->
    <span class="close-btn" id="closeRegisterBtn">&times;</span><!-- Botón cerrar -->

    <h2>Registrar Usuario</h2><!-- Título del modal -->

    <%-- ======================================================== --%>
    <%-- BLOQUE: Mostrar errores o mensaje de éxito de registro --%>
    <%
      String errorRegister = (String) request.getAttribute("errorRegister");
      String msgSuccess    = (String) request.getAttribute("msgSuccess");
      if (errorRegister != null) {  // Si hay error de registro
    %>
    <div class="error"><%= errorRegister %></div><!-- Muestra error de registro -->
    <% } else if (msgSuccess != null) { %>
    <div class="success"><%= msgSuccess %></div><!-- Muestra mensaje de éxito -->
    <% } %>
    <%-- FIN BLOQUE ERRORES/SUCCESS REGISTRO --%>

    <%-- ======================================================== --%>
    <%-- FORMULARIO: Registrar usuario --%>
    <form action="login" method="post">
      <input type="hidden" name="action" value="register" /><!-- Oculto para indicar acción register -->

      <label for="usuarioRegister">Usuario</label><br/> <!-- Etiqueta usuario registro -->
      <input type="text" name="usuario" id="usuarioRegister"/><br/> <!-- Campo usuario -->

      <label for="contrasenaRegister">Contraseña</label><br/> <!-- Etiqueta contraseña registro -->
      <input type="password" name="contrasena" id="contrasenaRegister"/><br/><br/> <!-- Campo contraseña -->

      <button type="submit">Registrar</button> <!-- Botón envío registro -->
    </form>
    <%-- FIN FORMULARIO REGISTRO --%>
  </div>
</div>
<!-- FIN MODAL DE REGISTRO -->

<script>
  // Variables para controlar apertura/cierre del modal
  const openBtn  = document.getElementById('openRegisterBtn');
  const modal    = document.getElementById('registerModal');
  const closeBtn = document.getElementById('closeRegisterBtn');

  // Al cargar la página, ocultar modal
  window.onload = function() {
    modal.style.display = 'none';
  };

  // Al hacer clic en "Registrarse", mostrar modal
  openBtn.onclick = function() {
    modal.style.display = 'block';
  };

  // Al hacer clic en la "x", ocultar modal
  closeBtn.onclick = function() {
    modal.style.display = 'none';
  };

  // Si el usuario hace clic fuera del contenido, cerrar modal
  window.onclick = function(event) {
    if (event.target == modal) {
      modal.style.display = 'none';
    }
  };
</script>
</body>
</html>
