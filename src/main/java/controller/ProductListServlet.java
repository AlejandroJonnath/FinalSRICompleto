package controller; // Define el paquete donde se ubica este servlet

import jakarta.servlet.ServletException; // Importa excepción de errores en el servlet
import jakarta.servlet.annotation.WebServlet; // Importa anotación para mapear el servlet a una URL
import jakarta.servlet.http.*; // Importa clases HTTP: HttpServlet, HttpServletRequest, HttpServletResponse, HttpSession
import model.Producto; // Importa el modelo Producto
import services.ProductService; // Importa el servicio que maneja lógica de productos

import java.io.IOException; // Importa excepción de I/O
import java.util.List; // Importa la interfaz List

@WebServlet("/productos") // Mapea este servlet a la URL “/productos”
public class ProductListServlet extends HttpServlet { // Clase que extiende HttpServlet
    private final ProductService productService = new ProductService(); // Instancia el servicio de productos

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Método que maneja peticiones GET
        HttpSession session = request.getSession(false); // Obtiene la sesión existente sin crear nueva
        if (session == null || session.getAttribute("usuario") == null) { // Si no hay sesión o no hay usuario logueado
            response.sendRedirect(request.getContextPath() + "/login"); // Redirige al login
            return; // Termina la ejecución para no procesar más
        }

        List<Producto> productos = productService.listarProductos(); // Obtiene la lista de productos desde el servicio
        request.setAttribute("productos", productos); // Añade los productos como atributo de la petición

        String rol = (String) session.getAttribute("rol"); // Obtiene el rol del usuario desde la sesión
        request.setAttribute("rol", rol); // Añade el rol como atributo de la petición

        request.getRequestDispatcher("productos.jsp").forward(request, response); // Reenvía a la JSP que muestra la lista
    }
}
