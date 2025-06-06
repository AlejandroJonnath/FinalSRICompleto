package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Usuario;
import services.UserService;

import java.io.IOException;
import java.util.List;

@WebServlet("/usuarios")
public class UsuariosServlet extends HttpServlet {
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verificar sesión activa y rol ADMIN
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String rol = (String) session.getAttribute("rol");
        if (!"ADMIN".equalsIgnoreCase(rol)) {
            response.sendRedirect(request.getContextPath() + "/productos");
            return;
        }

        // Obtener lista de usuarios y mostrarla en Main.jsp
        List<Usuario> listaUsuarios = userService.obtenerTodosLosUsuarios();
        request.setAttribute("usuarios", listaUsuarios);
        request.setAttribute("vista", "usuarios");

        request.getRequestDispatcher("/usuarios.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action != null ? action.toLowerCase() : "") {
            case "crear":
                crearUsuario(request, response);
                break;
            case "editar":
                editarUsuario(request, response);
                break;
            case "eliminar":
                eliminarUsuario(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/usuarios");
                break;
        }
    }

    private void crearUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nombreUsuario = request.getParameter("usuario");
        String contrasena = request.getParameter("contrasena");

        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()
                || contrasena == null || contrasena.trim().isEmpty()) {
            request.setAttribute("errorCrear", "Debe completar usuario y contraseña.");
            doGet(request, response);
            return;
        }

        Usuario nuevo = new Usuario();
        nuevo.setUsuario(nombreUsuario.trim());
        nuevo.setContrasena(contrasena.trim());

        boolean creado = userService.registrarUsuario(nuevo);

        if (!creado) {
            request.setAttribute("errorCrear", "No se pudo crear usuario (quizá ya existe).");
        } else {
            request.setAttribute("msgCrear", "Usuario creado correctamente.");
        }

        doGet(request, response);
    }

    private void editarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        String nuevoUsuario = request.getParameter("usuario");
        String nuevaContrasena = request.getParameter("contrasena");

        if (idParam == null || idParam.trim().isEmpty()) {
            request.setAttribute("errorEditar", "ID de usuario inválido.");
            doGet(request, response);
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            request.setAttribute("errorEditar", "ID de usuario inválido.");
            doGet(request, response);
            return;
        }

        if (nuevoUsuario == null || nuevoUsuario.trim().isEmpty()
                || nuevaContrasena == null || nuevaContrasena.trim().isEmpty()) {
            request.setAttribute("errorEditar", "Usuario y contraseña no pueden estar vacíos.");
            doGet(request, response);
            return;
        }

        Usuario existente = userService.buscarPorId(id);
        if (existente == null) {
            request.setAttribute("errorEditar", "El usuario no existe.");
            doGet(request, response);
            return;
        }

        existente.setUsuario(nuevoUsuario.trim());
        existente.setContrasena(nuevaContrasena.trim());

        boolean actualizado = userService.actualizarUsuario(existente);

        if (!actualizado) {
            request.setAttribute("errorEditar", "No se pudo actualizar datos del usuario.");
        } else {
            request.setAttribute("msgEditar", "Usuario actualizado correctamente.");
        }

        doGet(request, response);
    }

    private void eliminarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");

        if (idParam == null || idParam.trim().isEmpty()) {
            request.setAttribute("errorEliminar", "ID de usuario inválido.");
            doGet(request, response);
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            request.setAttribute("errorEliminar", "ID de usuario inválido.");
            doGet(request, response);
            return;
        }

        boolean eliminado = userService.eliminarUsuarioPorId(id);

        if (!eliminado) {
            request.setAttribute("errorEliminar", "No se pudo eliminar el usuario.");
        } else {
            request.setAttribute("msgEliminar", "Usuario eliminado correctamente.");
        }

        doGet(request, response);
    }
}
