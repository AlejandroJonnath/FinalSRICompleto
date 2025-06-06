package controller; // Define el paquete donde se ubica este servlet

import jakarta.servlet.ServletException; // Importa excepción lanzada por errores en el servlet
import jakarta.servlet.annotation.WebServlet; // Importa anotación para mapear el servlet a una URL
import jakarta.servlet.http.*; // Importa clases HTTP: HttpServlet, HttpServletRequest, HttpServletResponse, HttpSession
import model.Usuario; // Importa el modelo Usuario
import services.UserService; // Importa el servicio que maneja lógica de usuarios

import java.io.IOException; // Importa excepción de I/O
import java.util.List; // Importa interfaz List para colecciones

@WebServlet("/usuarios") // Mapea este servlet a la URL “/usuarios”
public class UsuariosServlet extends HttpServlet { // Clase que extiende HttpServlet
    private final UserService userService = new UserService(); // Instancia el servicio de usuario

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Maneja peticiones GET
        // Verificar sesión activa y rol ADMIN
        HttpSession session = request.getSession(false); // Obtiene la sesión existente sin crear nueva
        if (session == null || session.getAttribute("usuario") == null) { // Si no hay sesión o no hay usuario logueado
            response.sendRedirect(request.getContextPath() + "/login"); // Redirige al login
            return; // Termina ejecución
        }

        String rol = (String) session.getAttribute("rol"); // Obtiene el rol del usuario desde la sesión
        if (!"ADMIN".equalsIgnoreCase(rol)) { // Si el rol no es ADMIN
            response.sendRedirect(request.getContextPath() + "/productos"); // Redirige a la lista de productos
            return; // Termina ejecución
        }

        // Obtener lista de usuarios y mostrarla en usuarios.jsp
        List<Usuario> listaUsuarios = userService.obtenerTodosLosUsuarios(); // Llama al servicio para obtener todos los usuarios
        request.setAttribute("usuarios", listaUsuarios); // Añade la lista como atributo de la petición
        request.setAttribute("vista", "usuarios"); // Señala a la vista qué sección mostrar

        request.getRequestDispatcher("/usuarios.jsp").forward(request, response); // Reenvía a la JSP de usuarios
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Maneja peticiones POST
        String action = request.getParameter("action"); // Lee el parámetro "action" para decidir la operación

        switch (action != null ? action.toLowerCase() : "") { // Evalúa acción en minúsculas o vacío si null
            case "crear": // Si la acción es crear
                crearUsuario(request, response); // Llama al método de creación
                break;
            case "editar": // Si la acción es editar
                editarUsuario(request, response); // Llama al método de edición
                break;
            case "eliminar": // Si la acción es eliminar
                eliminarUsuario(request, response); // Llama al método de eliminación
                break;
            default: // Si no coincide
                response.sendRedirect(request.getContextPath() + "/usuarios"); // Redirige a la lista de usuarios
                break;
        }
    }

    private void crearUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Método para crear un nuevo usuario
        String nombreUsuario = request.getParameter("usuario"); // Obtiene el nombre de usuario del formulario
        String contrasena = request.getParameter("contrasena"); // Obtiene la contraseña del formulario

        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()
                || contrasena == null || contrasena.trim().isEmpty()) { // Valida campos vacíos
            request.setAttribute("errorCrear", "Debe completar usuario y contraseña."); // Mensaje de error
            doGet(request, response); // Vuelve al doGet para recargar la vista con error
            return; // Termina método
        }

        Usuario nuevo = new Usuario(); // Crea nueva instancia de Usuario
        nuevo.setUsuario(nombreUsuario.trim()); // Asigna nombre de usuario sin espacios
        nuevo.setContrasena(contrasena.trim()); // Asigna contraseña sin espacios

        boolean creado = userService.registrarUsuario(nuevo); // Intenta registrar el usuario

        if (!creado) { // Si falla creación
            request.setAttribute("errorCrear", "No se pudo crear usuario (quizá ya existe)."); // Mensaje de error
        } else { // Si se creó correctamente
            request.setAttribute("msgCrear", "Usuario creado correctamente."); // Mensaje de éxito
        }

        doGet(request, response); // Recarga la vista de lista con mensajes
    }

    private void editarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Método para editar un usuario existente
        String idParam = request.getParameter("id"); // Obtiene parámetro id
        String nuevoUsuario = request.getParameter("usuario"); // Obtiene nuevo nombre de usuario
        String nuevaContrasena = request.getParameter("contrasena"); // Obtiene nueva contraseña

        if (idParam == null || idParam.trim().isEmpty()) { // Valida id vacío
            request.setAttribute("errorEditar", "ID de usuario inválido."); // Mensaje de error
            doGet(request, response); // Recarga vista con error
            return; // Termina método
        }

        int id; // Variable para id convertido
        try {
            id = Integer.parseInt(idParam); // Convierte id a entero
        } catch (NumberFormatException e) { // Manejo de formato inválido
            request.setAttribute("errorEditar", "ID de usuario inválido."); // Mensaje de error
            doGet(request, response); // Recarga vista con error
            return; // Termina método
        }

        if (nuevoUsuario == null || nuevoUsuario.trim().isEmpty()
                || nuevaContrasena == null || nuevaContrasena.trim().isEmpty()) { // Valida campos vacíos
            request.setAttribute("errorEditar", "Usuario y contraseña no pueden estar vacíos."); // Mensaje de error
            doGet(request, response); // Recarga vista con error
            return; // Termina método
        }

        Usuario existente = userService.buscarPorId(id); // Obtiene usuario existente por ID
        if (existente == null) { // Si no existe
            request.setAttribute("errorEditar", "El usuario no existe."); // Mensaje de error
            doGet(request, response); // Recarga vista con error
            return; // Termina método
        }

        existente.setUsuario(nuevoUsuario.trim()); // Asigna nuevo nombre al usuario existente
        existente.setContrasena(nuevaContrasena.trim()); // Asigna nueva contraseña

        boolean actualizado = userService.actualizarUsuario(existente); // Intenta actualizar el usuario

        if (!actualizado) { // Si falla actualización
            request.setAttribute("errorEditar", "No se pudo actualizar datos del usuario."); // Mensaje de error
        } else { // Si se actualiza correctamente
            request.setAttribute("msgEditar", "Usuario actualizado correctamente."); // Mensaje de éxito
        }

        doGet(request, response); // Recarga la vista de lista con mensajes
    }

    private void eliminarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Método para eliminar un usuario por ID
        String idParam = request.getParameter("id"); // Obtiene parámetro id

        if (idParam == null || idParam.trim().isEmpty()) { // Valida id vacío
            request.setAttribute("errorEliminar", "ID de usuario inválido."); // Mensaje de error
            doGet(request, response); // Recarga vista con error
            return; // Termina método
        }

        int id; // Variable para id convertido
        try {
            id = Integer.parseInt(idParam); // Convierte id a entero
        } catch (NumberFormatException e) { // Manejo de formato inválido
            request.setAttribute("errorEliminar", "ID de usuario inválido."); // Mensaje de error
            doGet(request, response); // Recarga vista con error
            return; // Termina método
        }

        boolean eliminado = userService.eliminarUsuarioPorId(id); // Intenta eliminar el usuario

        if (!eliminado) { // Si falla eliminación
            request.setAttribute("errorEliminar", "No se pudo eliminar el usuario."); // Mensaje de error
        } else { // Si se elimina correctamente
            request.setAttribute("msgEliminar", "Usuario eliminado correctamente."); // Mensaje de éxito
        }

        doGet(request, response); // Recarga la vista de lista con mensajes
    }
}
