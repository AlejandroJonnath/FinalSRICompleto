package controller; // Define el paquete donde se ubica este servlet

import jakarta.servlet.ServletException; // Importa excepción de errores en el servlet
import jakarta.servlet.annotation.WebServlet; // Importa la anotación para mapear el servlet
import jakarta.servlet.http.*; // Importa clases HTTP: HttpServlet, HttpServletRequest, HttpServletResponse, HttpSession
import model.Usuario; // Importa el modelo Usuario
import services.UserService; // Importa el servicio que maneja lógica de usuarios

import java.io.IOException; // Importa excepción de I/O

@WebServlet("/login") // 1) Mapea este servlet a la URL "/login"
public class LoginServlet extends HttpServlet { // 1) Define clase servlet que extiende HttpServlet para manejar peticiones HTTP

    private final UserService userService = new UserService(); // 1) Instancia del servicio que maneja la lógica de usuarios

    // ==========================
    // SECCIÓN: Manejo de GET
    // ==========================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // 2) Maneja peticiones GET (mostrar formulario o redirigir)

        // 3) Obtiene la sesión actual sin crear una nueva
        HttpSession session = request.getSession(false);

        // 4) Verifica si ya hay un usuario logueado en sesión
        if (session != null && session.getAttribute("usuario") != null) {
            // 5) Si existe usuario, redirige a la página de productos
            response.sendRedirect(request.getContextPath() + "/productos");
            return; // 6) Termina ejecución para evitar mostrar login nuevamente
        }

        // 7) Si no hay sesión o usuario, muestra el formulario de login
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    // ==========================
    // SECCIÓN: Manejo de POST
    // ==========================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // 2) Maneja peticiones POST (login o registro)

        // 3) Lee parámetro "action" para determinar si es login o registro
        String action = request.getParameter("action");

        // 4) Si la acción es "register", llama al método de registro
        if ("register".equalsIgnoreCase(action)) {
            registrarUsuario(request, response);
        } else { // 5) Si no, asume login y llama al método de login
            loginUsuario(request, response);
        }
    }

    // ==========================
    // SECCIÓN: Método loginUsuario
    // ==========================
    /**
     * Procesa el login de un usuario.
     * Si se llega a modificar esto, podría afectar la autenticación y seguridad del sistema.
     */
    private void loginUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // 2) Método privado para manejar login

        // 3) Obtiene los parámetros enviados desde el formulario
        String usuarioParam = request.getParameter("usuario");
        String contrasenaParam = request.getParameter("contrasena");

        // 4) Valida que los campos no estén vacíos ni nulos
        if (usuarioParam == null || usuarioParam.trim().isEmpty()
                || contrasenaParam == null || contrasenaParam.trim().isEmpty()) {
            // 5) Si hay error, establece mensaje y reenvía a formulario
            request.setAttribute("error", "Debe ingresar usuario y contraseña.");
            request.setAttribute("usuario", usuarioParam != null ? usuarioParam : "");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return; // 6) Termina método para evitar continuar con datos inválidos
        }

        // 7) Llama al servicio para validar credenciales (trim elimina espacios)
        Usuario u = userService.validarUsuario(usuarioParam.trim(), contrasenaParam.trim());

        // 8) Si el usuario existe y las credenciales son correctas
        if (u != null) {
            // 9) Crea o recupera sesión HTTP
            HttpSession session = request.getSession();
            // 10) Guarda el nombre de usuario en la sesión
            session.setAttribute("usuario", u.getUsuario());

            // 11) Asigna rol según si el usuario es admin o no
            if ("admin".equalsIgnoreCase(u.getUsuario())) {
                session.setAttribute("rol", "ADMIN");
            } else {
                session.setAttribute("rol", "USER");
            }

            // 12) Inicializa carrito vacío en sesión
            session.setAttribute("cart", new java.util.HashMap<Integer, Integer>());

            // 13) Redirige a página principal de productos
            response.sendRedirect(request.getContextPath() + "/productos");

        } else { // 14) Si credenciales incorrectas, muestra error en login
            request.setAttribute("error", "Usuario o contraseña incorrectos");
            request.setAttribute("usuario", usuarioParam);
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    // ==========================
    // SECCIÓN: Método registrarUsuario
    // ==========================
    /**
     * Procesa el registro de un nuevo usuario.
     * Si se llega a modificar esto, podría afectar la creación de usuarios nuevos y la integridad del sistema.
     */
    private void registrarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // 2) Método privado para manejar registro

        // 3) Obtiene parámetros de usuario y contraseña desde el formulario
        String usuarioParam = request.getParameter("usuario");
        String contrasenaParam = request.getParameter("contrasena");

        // 4) Valida que no estén vacíos ni nulos
        if (usuarioParam == null || usuarioParam.trim().isEmpty()
                || contrasenaParam == null || contrasenaParam.trim().isEmpty()) {
            // 5) Si falla validación, muestra error y vuelve al formulario
            request.setAttribute("errorRegister", "Debe ingresar usuario y contraseña para registrarse.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return; // 6) Termina para evitar registrar con datos inválidos
        }

        // 7) Crea instancia de Usuario con los datos recibidos
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsuario(usuarioParam.trim());
        nuevoUsuario.setContrasena(contrasenaParam.trim()); // Nota: encriptar en servicio/repositorio

        // 8) Intenta registrar el nuevo usuario usando el servicio
        boolean registrado = userService.registrarUsuario(nuevoUsuario);

        // 9) Si registro exitoso, muestra mensaje de éxito
        if (registrado) {
            request.setAttribute("msgSuccess", "Registro exitoso. Por favor, inicie sesión.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } else { // 10) Si falla registro, muestra mensaje de error
            request.setAttribute("errorRegister", "El usuario ya existe o no se pudo registrar.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}
