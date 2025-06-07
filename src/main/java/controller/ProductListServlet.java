package controller; // Define el paquete donde se ubica este servlet

import jakarta.servlet.ServletException; // Importa excepción de errores en el servlet
import jakarta.servlet.annotation.WebServlet; // Importa anotación para mapear el servlet a una URL
import jakarta.servlet.http.*; // Importa clases HTTP: HttpServlet, HttpServletRequest, HttpServletResponse, HttpSession
import model.Producto; // Importa el modelo Producto
import services.ProductService; // Importa el servicio que maneja lógica de productos

import java.io.IOException; // Importa excepción de I/O
import java.util.List; // Importa la interfaz List

@WebServlet("/productos") // 1) Mapea este servlet a la URL "/productos"
public class ProductListServlet extends HttpServlet { // 1) Define la clase servlet que extiende HttpServlet para manejar peticiones HTTP

    private final ProductService productService = new ProductService(); // 1) Instancia del servicio que maneja la lógica de productos

    // ==========================
    // SECCIÓN: Manejo de GET para mostrar lista de productos
    // ==========================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // 2) Método que maneja peticiones GET

        // 3) Obtiene la sesión HTTP actual sin crear una nueva
        HttpSession session = request.getSession(false);

        // 4) Válida que exista sesión y que haya un usuario logueado
        if (session == null || session.getAttribute("usuario") == null) {
            // 5) Si no hay usuario autenticado, redirige a la página de login
            response.sendRedirect(request.getContextPath() + "/login");
            return; // 6) Termina la ejecución para evitar procesar más sin usuario
        }

        // 7) Llama al servicio para obtener la lista completa de productos
        List<Producto> productos = productService.listarProductos();

        // 8) Añade la lista de productos como atributo a la petición para usar en la vista JSP
        request.setAttribute("productos", productos);

        // 9) Obtiene el rol del usuario desde la sesión
        String rol = (String) session.getAttribute("rol");

        // 10) Añade el rol como atributo de la petición para que la vista pueda mostrar contenido según permisos
        request.setAttribute("rol", rol);

        // 11) Reenvía la petición y respuesta a la JSP "productos.jsp" para mostrar la lista de productos
        request.getRequestDispatcher("productos.jsp").forward(request, response);
    }
}
