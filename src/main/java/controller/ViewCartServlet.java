package controller; // Define el paquete donde se ubica este servlet

import jakarta.servlet.ServletException; // Importa excepción de errores en el servlet
import jakarta.servlet.annotation.WebServlet; // Importa anotación para mapear el servlet a una URL
import jakarta.servlet.http.*; // Importa clases HTTP: HttpServlet, HttpServletRequest, HttpServletResponse, HttpSession
import model.Producto; // Importa el modelo Producto
import services.ProductService; // Importa el servicio que maneja lógica de productos

import java.io.IOException; // Importa excepción de I/O
import java.math.BigDecimal; // Importa BigDecimal para cálculos con precisión decimal
import java.util.*; // Importa colecciones: List, Map, HashMap, ArrayList, etc.

@WebServlet("/cart") // Mapea este servlet a la URL “/cart”
public class ViewCartServlet extends HttpServlet { // Clase que extiende HttpServlet
    private final ProductService productService = new ProductService(); // Instancia el servicio de productos

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Maneja peticiones GET
        HttpSession session = request.getSession(false); // Obtiene la sesión existente, sin crear una nueva
        if (session == null || session.getAttribute("usuario") == null) { // Si no hay sesión o no hay usuario logueado
            response.sendRedirect(request.getContextPath() + "/login"); // Redirige al login
            return; // Termina la ejecución
        }

        @SuppressWarnings("unchecked") // Suprime advertencia de casteo inseguro
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart"); // Obtiene el carrito de la sesión
        if (cart == null) { // Si no existe carrito en la sesión
            cart = new HashMap<>(); // Crea un carrito vacío
        }

        List<Map<String, Object>> items = new ArrayList<>(); // Lista para almacenar detalles de cada ítem
        BigDecimal totalGeneral = BigDecimal.ZERO; // Inicializa el total general en cero
        BigDecimal totalIva = BigDecimal.ZERO;

        BigDecimal porcentajeIva = new BigDecimal("0.15");


        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            int productoId = entry.getKey();
            int cantidad = entry.getValue();
            Producto p = productService.obtenerPorId(productoId);
            if (p != null) {
                BigDecimal subtotal = p.getPrecio().multiply(new BigDecimal(cantidad));
                BigDecimal ivaProducto = subtotal.multiply(porcentajeIva);

                totalGeneral = totalGeneral.add(subtotal);
                totalIva = totalIva.add(ivaProducto);

                Map<String, Object> item = new HashMap<>();
                item.put("producto", p);
                item.put("cantidad", cantidad);
                item.put("subtotal", subtotal);
                item.put("iva", ivaProducto); //

                items.add(item);
            }
        }

        BigDecimal totalConIva = totalGeneral.add(totalIva);

        request.setAttribute("items", items);
        request.setAttribute("totalGeneral", totalGeneral);
        request.setAttribute("totalIva", totalIva);
        request.setAttribute("totalConIva", totalConIva);

        request.getRequestDispatcher("cart.jsp").forward(request, response);
    }
}
