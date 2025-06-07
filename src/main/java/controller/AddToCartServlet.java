package controller; // Define el paquete donde está esta clase

import jakarta.servlet.ServletException; // Importa la excepción ServletException para manejo de errores en servlets
import jakarta.servlet.annotation.WebServlet; // Importa la anotación para mapear el servlet a una URL específica
import jakarta.servlet.http.*; // Importa las clases HttpServlet, HttpServletRequest, HttpServletResponse y HttpSession necesarias para manejar peticiones HTTP

import java.io.IOException; // Importa la excepción IOException para manejo de errores de entrada/salida
import java.util.Map; // Importa la interfaz Map para manejar colecciones clave-valor (aquí para el carrito)

@WebServlet("/addToCart") // Define que este servlet responderá a la URL "/addToCart"
public class AddToCartServlet extends HttpServlet { // Declara la clase que hereda de HttpServlet para manejar peticiones HTTP

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Método que maneja peticiones POST para añadir productos al carrito

        // 1) Verificar que exista una sesión activa y que el usuario esté logueado
        HttpSession session = request.getSession(false); // Obtiene la sesión actual sin crear una nueva si no existe
        if (session == null || session.getAttribute("usuario") == null) { // Si no hay sesión o no hay usuario logueado
            response.sendRedirect(request.getContextPath() + "/login"); // Redirige al login para autenticación
            return; // Termina la ejecución para evitar continuar sin usuario válido
        }

        // 2) Leer parámetros del formulario: ID del producto y cantidad a añadir
        String idParam = request.getParameter("productoId"); // Obtiene el parámetro "productoId"
        String cantidadParam = request.getParameter("cantidad"); // Obtiene el parámetro "cantidad"

        // 3) Validar que ambos parámetros estén presentes
        if (idParam == null || cantidadParam == null) { // Si falta alguno
            response.sendRedirect(request.getContextPath() + "/productos?error=missingParams"); // Redirige con error de parámetros faltantes
            return; // Termina la ejecución para evitar procesar datos incompletos
        }

        int productoId; // Variable para almacenar el ID convertido a entero
        int cantidad;   // Variable para almacenar la cantidad convertida a entero

        try {
            // 4) Convertir parámetros a enteros y validar que la cantidad sea positiva
            productoId = Integer.parseInt(idParam.trim()); // Convierte el ID del producto a entero, eliminando espacios extras
            cantidad = Integer.parseInt(cantidadParam.trim()); // Convierte la cantidad a entero

            if (cantidad <= 0) { // Si la cantidad no es válida (menor o igual a 0)
                throw new NumberFormatException("Cantidad debe ser mayor a 0"); // Lanza excepción para manejo de error
            }

        } catch (NumberFormatException e) { // Captura cualquier error de conversión o valor inválido
            response.sendRedirect(request.getContextPath() + "/productos?error=invalidInput"); // Redirige con error de entrada inválida
            return; // Termina la ejecución para evitar datos incorrectos
        }

        // 5) Obtener el carrito de la sesión (Map donde la clave es el productoId y el valor es la cantidad)
        @SuppressWarnings("unchecked") // Suprime advertencias de tipo inseguro debido a casteo
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart"); // Obtiene el carrito actual

        if (cart == null) { // Si el carrito no existe aún en la sesión
            cart = new java.util.HashMap<>(); // Crea un nuevo carrito vacío
        }

        // 6) Actualizar la cantidad del producto en el carrito
        int cantidadPrevia = cart.getOrDefault(productoId, 0); // Obtiene la cantidad actual del producto en el carrito, o 0 si no existe
        cart.put(productoId, cantidadPrevia + cantidad); // Suma la cantidad nueva a la previa y actualiza el carrito

        // 7) Guardar el carrito actualizado en la sesión
        session.setAttribute("cart", cart);

        // 8) Redirigir a la página de productos con mensaje de éxito
        response.sendRedirect(request.getContextPath() + "/productos?success=added");
    }
}
