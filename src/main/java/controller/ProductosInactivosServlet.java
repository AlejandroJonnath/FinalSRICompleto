package controller; // Define el paquete donde se ubica este servlet

import jakarta.servlet.ServletException; // Excepción lanzada en errores de servlet
import jakarta.servlet.annotation.WebServlet; // Anotación para mapear el servlet a una URL
import jakarta.servlet.http.*; // Clases HTTP: HttpServlet, HttpServletRequest, HttpServletResponse, HttpSession
import model.Producto; // Modelo de producto
import services.ProductService; // Servicio que maneja la lógica de productos

import java.io.IOException; // Excepción de I/O
import java.util.List; // Interfaz List para colecciones

@WebServlet("/productosInactivos") // Mapea este servlet a la URL “/productosInactivos”
public class ProductosInactivosServlet extends HttpServlet { // Clase que extiende HttpServlet
    private final ProductService productService = new ProductService(); // Instancia el servicio de productos

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Método que maneja peticiones GET
        // Solo ADMIN puede ver inactivos
        HttpSession session = request.getSession(false); // Obtiene la sesión existente sin crear nueva
        if (session == null || !"ADMIN".equals(session.getAttribute("rol"))) { // Si no hay sesión o el rol no es ADMIN
            response.sendRedirect(request.getContextPath() + "/productos"); // Redirige a la lista de productos activos
            return; // Termina la ejecución para no procesar más
        }

        // Obtener lista de inactivos
        List<Producto> inactivos = productService.listarProductosInactivos(); // Llama al servicio para listar productos inactivos
        request.setAttribute("productosInactivos", inactivos); // Añade la lista como atributo de la petición

        // Enviar al JSP correspondiente
        request.getRequestDispatcher("productosInactivos.jsp").forward(request, response); // Reenvía a la vista de productos inactivos
    }
}
