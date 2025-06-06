package controller; // Define el paquete donde está esta clase

import jakarta.servlet.ServletException; // Importa la excepción ServletException
import jakarta.servlet.annotation.WebServlet; // Importa la anotación para mapear el servlet a una URL
import jakarta.servlet.http.*; // Importa clases HTTP: HttpServlet, HttpServletRequest, HttpServletResponse y HttpSession
import model.DetalleFactura; // Importa la clase DetalleFactura del modelo
import model.Factura; // Importa la clase Factura del modelo
import model.Producto; // Importa la clase Producto del modelo
import services.InvoiceService; // Importa el servicio que maneja facturas
import services.ProductService; // Importa el servicio que maneja productos

import java.io.IOException; // Importa la excepción IOException
import java.math.BigDecimal; // Importa BigDecimal para cálculos de dinero
import java.util.Map; // Importa la interfaz Map para colecciones clave-valor

@WebServlet("/checkout") // Mapea este servlet a la URL “/checkout”
public class CheckoutServlet extends HttpServlet { // Declara la clase que extiende HttpServlet
    private final InvoiceService invoiceService = new InvoiceService(); // Instancia el servicio de facturas
    private final ProductService productService = new ProductService(); // Instancia el servicio de productos

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Método que maneja peticiones POST
        HttpSession session = request.getSession(false); // Obtiene la sesión existente, sin crear una nueva
        if (session == null || session.getAttribute("usuario") == null) { // Si no hay sesión o no hay usuario logueado
            response.sendRedirect(request.getContextPath() + "/login"); // Redirige al login
            return; // Termina la ejecución
        }

        @SuppressWarnings("unchecked") // Suprime advertencia de casteo inseguro
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart"); // Obtiene el carrito de la sesión
        if (cart == null || cart.isEmpty()) { // Si el carrito es nulo o está vacío
            request.setAttribute("mensajeError", "El carrito está vacío."); // Define mensaje de error
            request.getRequestDispatcher("cart.jsp").forward(request, response); // Muestra la vista del carrito
            return; // Termina la ejecución
        }

        String clienteNombre = request.getParameter("clienteNombre"); // Obtiene el nombre del cliente
        String clienteCedula = request.getParameter("clienteCedula"); // Obtiene la cédula del cliente

        // Recalcular total general
        BigDecimal totalGeneral = BigDecimal.ZERO; // Inicializa el total en cero
        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) { // Recorre cada entrada del carrito
            int productoId = entry.getKey(); // ID del producto
            int cantidad = entry.getValue(); // Cantidad comprada
            Producto p = productService.obtenerPorId(productoId); // Obtiene el producto por su ID
            if (p != null) { // Si el producto existe
                BigDecimal subtotal = p.getPrecio().multiply(new BigDecimal(cantidad)); // Calcula subtotal
                totalGeneral = totalGeneral.add(subtotal); // Suma el total general
            }
        }

        // Insertar factura
        Factura factura = new Factura(); // Crea una nueva factura
        factura.setClienteNombre(clienteNombre); // Asigna el nombre del cliente
        factura.setClienteCedula(clienteCedula); // Asigna la cédula del cliente
        factura.setTotal(totalGeneral); // Asigna el total calculado

        int facturaId = invoiceService.insertarFactura(factura); // Inserta la factura y devuelve su ID
        if (facturaId == -1) { // Si hubo un error al insertar
            request.setAttribute("mensajeError", "Error al procesar la factura."); // Define mensaje de error
            request.getRequestDispatcher("cart.jsp").forward(request, response); // Muestra la vista del carrito
            return; // Termina la ejecución
        }

        // Insertar los detalles y reducir stock
        boolean todoBien = true; // Indicador de éxito para todos los detalles
        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) { // Recorre cada entrada del carrito
            int productoId = entry.getKey(); // ID del producto
            int cantidad = entry.getValue(); // Cantidad comprada
            Producto p = productService.obtenerPorId(productoId); // Obtiene el producto por su ID
            if (p != null) { // Si el producto existe
                BigDecimal precioUnitario = p.getPrecio(); // Obtiene el precio unitario
                BigDecimal subtotal = precioUnitario.multiply(new BigDecimal(cantidad)); // Calcula subtotal

                DetalleFactura detalle = new DetalleFactura(); // Crea un nuevo detalle de factura
                detalle.setFacturaId(facturaId); // Asigna el ID de la factura
                detalle.setProductoId(productoId); // Asigna el ID del producto
                detalle.setCantidad(cantidad); // Asigna la cantidad
                detalle.setPrecioUnitario(precioUnitario); // Asigna el precio unitario
                detalle.setSubtotal(subtotal); // Asigna el subtotal

                boolean okDetalle = invoiceService.insertarDetalle(detalle); // Inserta el detalle en la base
                boolean okStock = invoiceService.reducirStock(productoId, cantidad); // Reduce el stock del producto

                if (!okDetalle || !okStock) { // Si falla alguno de los dos
                    todoBien = false; // Marca el proceso como fallido
                    break; // Sale del bucle
                }
            }
        }

        if (!todoBien) { // Si hubo algún error al insertar detalles o actualizar stock
            request.setAttribute("mensajeError", "Error al guardar detalles de la factura o al actualizar stock."); // Define mensaje de error
            request.getRequestDispatcher("cart.jsp").forward(request, response); // Muestra la vista del carrito
            return; // Termina la ejecución
        }

        // Vaciar carrito en sesión
        session.setAttribute("cart", new java.util.HashMap<Integer, Integer>()); // Reemplaza el carrito con uno vacío

        request.setAttribute("mensajeExito", "Compra realizada con éxito. ID de factura: " + facturaId); // Define mensaje de éxito
        request.setAttribute("invoiceId", facturaId); // Pasa el ID de la factura a la vista
        request.getRequestDispatcher("compraExitosa.jsp").forward(request, response); // Muestra la vista de éxito de compra
    }
}
