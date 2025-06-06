package controller; // Define el paquete donde está esta clase

import jakarta.servlet.ServletException; // Importa la excepción ServletException
import jakarta.servlet.annotation.WebServlet; // Importa la anotación para mapear el servlet a una URL
import jakarta.servlet.http.*; // Importa clases HTTP: HttpServlet, HttpServletRequest, HttpServletResponse y HttpSession

import java.io.IOException; // Importa la excepción IOException
import java.util.Map; // Importa la interfaz Map para colecciones clave-valor

@WebServlet("/addToCart") // Mapea este servlet a la URL “/addToCart”
public class AddToCartServlet extends HttpServlet { // Declara la clase que extiende HttpServlet
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Método que maneja peticiones POST

        HttpSession session = request.getSession(false); // Obtiene la sesión existente sin crear una nueva
        if (session == null || session.getAttribute("usuario") == null) { // Si no hay sesión o no hay usuario logueado
            response.sendRedirect(request.getContextPath() + "/login"); // Redirige al formulario de login
            return; // Termina la ejecución para no procesar más
        }

        String idParam = request.getParameter("productoId"); // Obtiene el parámetro "productoId" del formulario
        String cantidadParam = request.getParameter("cantidad"); // Obtiene el parámetro "cantidad" del formulario

        if (idParam == null || cantidadParam == null) { // Sí falta alguno de los parámetros
            response.sendRedirect(request.getContextPath() + "/productos?error=missingParams"); // Redirige con error de parámetros faltantes
            return; // Termina la ejecución
        }

        int productoId; // Variable para almacenar el ID convertido a entero
        int cantidad;   // Variable para almacenar la cantidad convertida a entero

        try {
            productoId = Integer.parseInt(idParam.trim()); // Convierte el ID a entero, eliminando espacios
            cantidad = Integer.parseInt(cantidadParam.trim()); // Convierte la cantidad a entero, eliminando espacios

            if (cantidad <= 0) { // Si la cantidad no es mayor que 0
                throw new NumberFormatException("Cantidad debe ser mayor a 0"); // Fuerza excepción para manejo de error
            }

        } catch (NumberFormatException e) { // Captura errores de conversión o cantidad inválida
            response.sendRedirect(request.getContextPath() + "/productos?error=invalidInput"); // Redirige con error de entrada inválida
            return; // Termina la ejecución
        }

        @SuppressWarnings("unchecked") // Suprime advertencia de casteo inseguro
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart"); // Obtiene el carrito de la sesión
        if (cart == null) { // Si no existe carrito en la sesión
            cart = new java.util.HashMap<>(); // Crea un nuevo HashMap para el carrito
        }

        int cantidadPrevia = cart.getOrDefault(productoId, 0); // Obtiene la cantidad previa del producto en el carrito, o 0 si no existe
        cart.put(productoId, cantidadPrevia + cantidad); // Suma la nueva cantidad a la previa y actualiza el carrito

        session.setAttribute("cart", cart); // Guarda el carrito actualizado en la sesión
        response.sendRedirect(request.getContextPath() + "/productos?success=added"); // Redirige a la lista de productos con mensaje de éxito
    }
}
