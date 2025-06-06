package controller; // Define el paquete donde está esta clase

import jakarta.servlet.ServletException; // Importa la excepción ServletException
import jakarta.servlet.annotation.WebServlet; // Importa la anotación para mapear el servlet a una URL
import jakarta.servlet.http.*; // Importa clases HTTP: HttpServlet, HttpServletRequest, HttpServletResponse y HttpSession
import services.ProductService; // Importa el servicio que maneja la lógica de productos

import java.io.IOException; // Importa la excepción IOException

@WebServlet("/deleteProduct") // Mapea este servlet a la URL “/deleteProduct”
public class DeleteProductServlet extends HttpServlet { // Declara la clase que extiende HttpServlet
    private final ProductService productService = new ProductService(); // Crea una instancia del servicio de productos

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Método que maneja peticiones GET
        HttpSession session = request.getSession(false); // Obtiene la sesión existente, sin crear una nueva
        if (session == null || !"ADMIN".equals(session.getAttribute("rol"))) { // Si no hay sesión o el rol no es ADMIN
            response.sendRedirect(request.getContextPath() + "/productos"); // Redirige a la lista de productos
            return; // Termina la ejecución para evitar procesar más
        }

        String idParam = request.getParameter("id"); // Obtiene el parámetro "id" de la URL
        if (idParam != null) { // Comprueba que el parámetro no sea nulo
            try {
                int id = Integer.parseInt(idParam.trim()); // Convierte el parámetro a entero, eliminando espacios
                productService.desactivarProducto(id); // Llama al servicio para desactivar (soft‐delete) el producto
            } catch (NumberFormatException e) {
                // Ignorar formato inválido o loguear si lo deseas
            }
        }

        response.sendRedirect(request.getContextPath() + "/productos"); // Redirige de nuevo a la lista de productos
    }
}
