package controller; // Define el paquete donde está esta clase

import jakarta.servlet.ServletException; // Importa la excepción ServletException
import jakarta.servlet.annotation.WebServlet; // Importa la anotación para mapear el servlet
import jakarta.servlet.http.*; // Importa clases HTTP como HttpServlet, HttpServletRequest, HttpServletResponse y HttpSession
import services.ProductService; // Importa la clase ProductService que maneja la lógica de productos

import java.io.IOException; // Importa la excepción IOException

@WebServlet("/activateProduct") // Mapea este servlet a la URL “/activateProduct”
public class ActivateProductServlet extends HttpServlet { // Declara la clase que extiende HttpServlet
    private final ProductService productService = new ProductService(); // Crea una instancia de ProductService para usar sus métodos

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Método que maneja peticiones GET
        // Solo ADMIN puede reactivar
        HttpSession session = request.getSession(false); // Obtiene la sesión existente, sin crear una nueva
        if (session == null || !"ADMIN".equals(session.getAttribute("rol"))) { // Comprueba si no hay sesión o el rol no es ADMIN
            response.sendRedirect(request.getContextPath() + "/productos"); // Redirige a la lista de productos si no es ADMIN
            return; // Sale del método para evitar seguir ejecutando código
        }

        String idParam = request.getParameter("id"); // Lee el parámetro "id" de la URL
        if (idParam != null) { // Comprueba que el parámetro no sea nulo
            try {
                int id = Integer.parseInt(idParam.trim()); // Convierte el parámetro a entero, eliminando espacios
                productService.activarProducto(id); // Llama al servicio para activar el producto (activo = 1)
            } catch (NumberFormatException e) {
                // Ignorar o loguear el error de parseo
            }
        }

        // Redirigir de nuevo a la lista de inactivos
        response.sendRedirect(request.getContextPath() + "/productosInactivos"); // Envía al usuario a la vista de productos inactivos
    }
}
