package controller; // Define el paquete donde está esta clase

import jakarta.servlet.ServletException; // Importa la excepción ServletException necesaria para manejar errores en Servlets
import jakarta.servlet.annotation.WebServlet; // Importa la anotación para mapear la URL de acceso al servlet
import jakarta.servlet.http.*; // Importa clases del API HTTP: HttpServlet, HttpServletRequest, HttpServletResponse, HttpSession
import services.ProductService; // Importa la clase ProductService que contiene la lógica para manipular productos

import java.io.IOException; // Importa la excepción IOException necesaria para manejo de errores de entrada/salida

// Sección: Definición del Servlet y su URL de acceso
@WebServlet("/activateProduct") // Indica que este servlet responde a solicitudes con la URL relativa "/activateProduct"
public class ActivateProductServlet extends HttpServlet { // Declara la clase ActivateProductServlet que extiende HttpServlet para manejar solicitudes HTTP

    // Sección: Instancia de servicio para productos
    private final ProductService productService = new ProductService(); // Crea una instancia privada y final de ProductService para usar métodos relacionados con productos

    // Sección: Manejo de peticiones GET para activar un producto
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Método que procesa solicitudes GET, puede lanzar excepciones ServletException e IOException

        // Sección: Validación de sesión y permisos de usuario
        HttpSession session = request.getSession(false); // Obtiene la sesión HTTP actual, sin crear una nueva sesión si no existe

        // Comprueba que la sesión exista y que el atributo "rol" sea exactamente "ADMIN"
        if (session == null || !"ADMIN".equals(session.getAttribute("rol"))) {
            // Si no hay sesión o el usuario no es ADMIN:
            response.sendRedirect(request.getContextPath() + "/productos"); // Redirige al listado general de productos
            return; // Termina la ejecución del método para evitar procesamiento adicional
        }

        // Sección: Lectura y validación del parámetro "id" recibido en la URL
        String idParam = request.getParameter("id"); // Obtiene el parámetro "id" de la solicitud HTTP GET
        if (idParam != null) { // Si el parámetro "id" no es nulo, procede con la activación
            try {
                int id = Integer.parseInt(idParam.trim()); // Convierte el parámetro "id" a entero, eliminando espacios en blanco
                productService.activarProducto(id); // Llama al método del servicio para activar el producto con el id dado (normalmente cambiar estado activo = 1)
            } catch (NumberFormatException e) {
                // Si el parámetro "id" no es un número válido, se ignora el error o se puede registrar (log)
            }
        }

        // Sección: Redirección tras la operación
        response.sendRedirect(request.getContextPath() + "/productosInactivos");
        // Redirige al usuario a la lista de productos inactivos, para confirmar visualmente la activación
    }

    /*
     * IMPORTANTE: Si se modifica el método doGet y se omite la validación de sesión o el rol ADMIN,
     * cualquier usuario podría activar productos, comprometiendo la seguridad de la aplicación.
     * Además, si se cambia la lógica de productService.activarProducto, podría afectar la consistencia
     * del estado activo de los productos en la base de datos.
     */
}
