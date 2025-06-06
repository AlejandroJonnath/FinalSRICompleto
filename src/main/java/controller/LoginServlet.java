package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Usuario;
import services.UserService;
import util.Encriptacion;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("usuario") != null) {
            response.sendRedirect(request.getContextPath() + "/productos");
            return;
        }
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("register".equalsIgnoreCase(action)) {
            registrarUsuario(request, response);
        } else {
            loginUsuario(request, response);
        }
    }

    private void loginUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String usuarioParam = request.getParameter("usuario");
        String contrasenaParam = request.getParameter("contrasena");

        if (usuarioParam == null || usuarioParam.trim().isEmpty()
                || contrasenaParam == null || contrasenaParam.trim().isEmpty()) {
            request.setAttribute("error", "Debe ingresar usuario y contraseña.");
            request.setAttribute("usuario", usuarioParam != null ? usuarioParam : "");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        // Usar validarUsuario en lugar de buscarPorUsuario
        Usuario u = userService.validarUsuario(usuarioParam.trim(), contrasenaParam.trim());

        if (u != null) {
            HttpSession session = request.getSession();
            session.setAttribute("usuario", u.getUsuario());

            if ("admin".equalsIgnoreCase(u.getUsuario())) {
                session.setAttribute("rol", "ADMIN");
            } else {
                session.setAttribute("rol", "USER");
            }
            session.setAttribute("cart", new java.util.HashMap<Integer, Integer>());

            response.sendRedirect(request.getContextPath() + "/productos");
        } else {
            request.setAttribute("error", "Usuario o contraseña incorrectos");
            request.setAttribute("usuario", usuarioParam);
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    private void registrarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String usuarioParam = request.getParameter("usuario");
        String contrasenaParam = request.getParameter("contrasena");

        if (usuarioParam == null || usuarioParam.trim().isEmpty()
                || contrasenaParam == null || contrasenaParam.trim().isEmpty()) {
            request.setAttribute("errorRegister", "Debe ingresar usuario y contraseña para registrarse.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsuario(usuarioParam.trim());
        nuevoUsuario.setContrasena(contrasenaParam.trim()); // La encriptación se maneja en el servicio/repositorio

        boolean registrado = userService.registrarUsuario(nuevoUsuario);
        if (registrado) {
            request.setAttribute("msgSuccess", "Registro exitoso. Por favor, inicie sesión.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } else {
            request.setAttribute("errorRegister", "El usuario ya existe o no se pudo registrar.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}