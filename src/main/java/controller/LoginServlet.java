package controller; // Define el paquete donde se ubica este servlet

import jakarta.servlet.ServletException; // Importa excepción de errores en el servlet
import jakarta.servlet.annotation.WebServlet; // Importa la anotación para mapear el servlet
import jakarta.servlet.http.*; // Importa clases HTTP: HttpServlet, HttpServletRequest, HttpServletResponse, HttpSession
import model.Usuario; // Importa el modelo Usuario
import services.UserService; // Importa el servicio que maneja lógica de usuarios
import util.Encriptacion; // Importa utilería para encriptación (no usada aquí directamente)

import java.io.IOException; // Importa excepción de I/O

@WebServlet("/login") // Mapea este servlet a la URL “/login”
public class LoginServlet extends HttpServlet { // Clase que extiende HttpServlet
    private final UserService userService = new UserService(); // Instancia el servicio de usuario

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Maneja peticiones GET
        HttpSession session = request.getSession(false); // Obtiene la sesión existente sin crear nueva
        if (session != null && session.getAttribute("usuario") != null) { // Si ya hay usuario en sesión
            response.sendRedirect(request.getContextPath() + "/productos"); // Redirige a la lista de productos
            return; // Termina el método
        }
        request.getRequestDispatcher("login.jsp").forward(request, response); // Muestra el formulario de login
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Maneja peticiones POST
        String action = request.getParameter("action"); // Lee parámetro "action" para distinguir login o registro
        if ("register".equalsIgnoreCase(action)) { // Si la acción es "register"
            registrarUsuario(request, response); // Llama al método de registro
        } else {
            loginUsuario(request, response); // En otro caso, llama al método de login
        }
    }

    private void loginUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Método para procesar login

        String usuarioParam = request.getParameter("usuario"); // Obtiene el nombre de usuario del formulario
        String contrasenaParam = request.getParameter("contrasena"); // Obtiene la contraseña del formulario

        if (usuarioParam == null || usuarioParam.trim().isEmpty()
                || contrasenaParam == null || contrasenaParam.trim().isEmpty()) { // Valida campos vacíos
            request.setAttribute("error", "Debe ingresar usuario y contraseña."); // Mensaje de error
            request.setAttribute("usuario", usuarioParam != null ? usuarioParam : ""); // Conserva usuario ingresado
            request.getRequestDispatcher("login.jsp").forward(request, response); // Reenvía al formulario
            return; // Termina el método
        }

        // Usar validarUsuario en lugar de buscarPorUsuario
        Usuario u = userService.validarUsuario(usuarioParam.trim(), contrasenaParam.trim()); // Valida credenciales

        if (u != null) { // Si el usuario es válido
            HttpSession session = request.getSession(); // Crea o recupera la sesión
            session.setAttribute("usuario", u.getUsuario()); // Guarda el nombre de usuario en sesión

            if ("admin".equalsIgnoreCase(u.getUsuario())) { // Si el usuario es "admin"
                session.setAttribute("rol", "ADMIN"); // Asigna rol ADMIN
            } else {
                session.setAttribute("rol", "USER"); // Asigna rol USER
            }
            session.setAttribute("cart", new java.util.HashMap<Integer, Integer>()); // Inicializa el carrito vacío

            response.sendRedirect(request.getContextPath() + "/productos"); // Redirige a la lista de productos
        } else { // Si credenciales inválidas
            request.setAttribute("error", "Usuario o contraseña incorrectos"); // Mensaje de error
            request.setAttribute("usuario", usuarioParam); // Conserva usuario ingresado
            request.getRequestDispatcher("login.jsp").forward(request, response); // Reenvía al formulario
        }
    }

    private void registrarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Método para procesar registro

        String usuarioParam = request.getParameter("usuario"); // Obtiene el nombre de usuario del formulario
        String contrasenaParam = request.getParameter("contrasena"); // Obtiene la contraseña del formulario

        if (usuarioParam == null || usuarioParam.trim().isEmpty()
                || contrasenaParam == null || contrasenaParam.trim().isEmpty()) { // Valida campos vacíos
            request.setAttribute("errorRegister", "Debe ingresar usuario y contraseña para registrarse."); // Mensaje de error
            request.getRequestDispatcher("login.jsp").forward(request, response); // Reenvía al formulario
            return; // Termina el método
        }

        Usuario nuevoUsuario = new Usuario(); // Crea nueva instancia de Usuario
        nuevoUsuario.setUsuario(usuarioParam.trim()); // Asigna el nombre de usuario
        nuevoUsuario.setContrasena(contrasenaParam.trim()); // Asigna la contraseña (encriptación en servicio/repositorio)

        boolean registrado = userService.registrarUsuario(nuevoUsuario); // Intenta registrar al usuario
        if (registrado) { // Si el registro fue exitoso
            request.setAttribute("msgSuccess", "Registro exitoso. Por favor, inicie sesión."); // Mensaje de éxito
            request.getRequestDispatcher("login.jsp").forward(request, response); // Reenvía al formulario
        } else { // Si falla el registro (usuario existe u otro error)
            request.setAttribute("errorRegister", "El usuario ya existe o no se pudo registrar."); // Mensaje de error
            request.getRequestDispatcher("login.jsp").forward(request, response); // Reenvía al formulario
        }
    }
}
