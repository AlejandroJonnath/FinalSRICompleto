package controller; // Define el paquete donde se ubica este servlet

import jakarta.servlet.ServletException; // Importa excepción de errores en el servlet
import jakarta.servlet.annotation.WebServlet; // Importa anotación para mapear el servlet a una URL
import jakarta.servlet.http.*; // Importa clases HTTP: HttpServlet, HttpServletRequest, HttpServletResponse, HttpSession

import java.io.IOException; // Importa excepción de I/O

@WebServlet("/logout") // Mapea este servlet a la URL “/logout”
public class LogoutServlet extends HttpServlet { // Clase que extiende HttpServlet
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Maneja peticiones GET
        HttpSession session = request.getSession(false); // Obtiene la sesión existente, sin crear una nueva
        if (session != null) { // Si existe una sesión activa
            session.invalidate(); // Invalida la sesión, eliminando todos sus atributos
        }
        response.sendRedirect(request.getContextPath() + "/login"); // Redirige al formulario de login
    }
}
