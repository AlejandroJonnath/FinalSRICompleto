package controller; // Define el paquete donde está esta clase

import jakarta.servlet.ServletException; // Importa la excepción ServletException
import jakarta.servlet.annotation.WebServlet; // Importa la anotación para mapear el servlet a una URL
import jakarta.servlet.http.*; // Importa clases HTTP: HttpServlet, HttpServletRequest, HttpServletResponse y HttpSession
import model.DetalleFactura; // Importa la clase DetalleFactura del modelo
import model.Factura; // Importa la clase Factura del modelo
import model.Producto; // Importa la clase Producto del modelo
import services.InvoiceService; // Importa el servicio que maneja facturas
import services.ProductService; // Importa el servicio que maneja productos
import util.ClaveAccesoUtil;

import java.io.IOException; // Importa la excepción IOException
import java.math.BigDecimal; // Importa BigDecimal para cálculos de dinero
import java.util.Date;
import java.util.Map; // Importa la interfaz Map para colecciones clave-valor

@WebServlet("/checkout") // Mapea este servlet a la URL “/checkout”
public class    CheckoutServlet extends HttpServlet { // Declara la clase que extiende HttpServlet
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
        String clienteDireccion = request.getParameter("clienteDireccion");
        String clienteTelefono = request.getParameter("clienteTelefono");
        String clienteEmail = request.getParameter("clienteEmail");

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
        factura.setFecha(new Date()); // ← AQUÍ pones la fecha actual para que no te dé error
        factura.setClienteNombre(clienteNombre); // Asigna el nombre del cliente
        factura.setClienteCedula(clienteCedula); // Asigna la cédula del cliente
        factura.setClienteDireccion(clienteDireccion);
        factura.setClienteEmail(clienteEmail);
        factura.setClienteTelefono(clienteTelefono); // <--- Este método debes tenerlo implementado
        factura.setTotal(totalGeneral); // Asigna el total calculado
        factura.setEstablecimiento("001");
        factura.setPuntoEmision("001");

        String siguienteSecuencial = invoiceService.obtenerSiguienteSecuencial("001", "001");
        factura.setSecuencial(siguienteSecuencial);

        factura.setClaveAcceso(ClaveAccesoUtil.generarClaveAcceso(
                "02062025", // usar date actual formateada idealmente
                "01", // tipoComprobante
                "1719284752001", // RUC empresa
                "1", // ambiente
                "001",
                "001",
                siguienteSecuencial,
                ClaveAccesoUtil.generarCodigoNumerico(),
                "1" // tipoEmision
        ));

        factura.setTipoEmision("1");

        int facturaId = invoiceService.insertarFactura(factura); // Inserta la factura y devuelve su ID
        if (facturaId == -1) { // Si hubo un error al insertar
            request.setAttribute("mensajeError", "Error al procesar la factura."); // Define mensaje de error
            request.getRequestDispatcher("cart.jsp").forward(request, response); // Muestra la vista del carrito
            return; // Termina la ejecución
        }

        // Insertar los detalles de factura y reducir stock
        boolean todoBien = true;
        BigDecimal porcentajeIva = new BigDecimal("0.15");
        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            int productoId = entry.getKey();
            int cantidad = entry.getValue();
            Producto p = productService.obtenerPorId(productoId);
            if (p != null) {
                BigDecimal precioUnitario = p.getPrecio();
                BigDecimal subtotal = precioUnitario.multiply(new BigDecimal(cantidad));

                // ------ NUEVO: calcular IVA (15%) ------
                BigDecimal ivaValor = subtotal.multiply(porcentajeIva);

                // ------ NUEVO: asignar descuento (si no hay, se pone 0.00) ------
                BigDecimal descuento = BigDecimal.ZERO;

                // Crear detalle de factura
                DetalleFactura detalle = new DetalleFactura();
                factura.setId(facturaId); // importante: asignar id generado de factura
                detalle.setFactura(factura);

                Producto producto = new Producto();
                producto.setId(productoId);
                detalle.setProducto(producto);

                detalle.setCantidad(cantidad);
                detalle.setPrecioUnitario(precioUnitario);
                detalle.setSubtotal(subtotal);

                // ------ NUEVO: setear IVA, descuento y stock ------
                detalle.setIva(new BigDecimal("15.00")); // más claro y seguro
                detalle.setIvaValor(ivaValor); // valor en $
                detalle.setDescuento(descuento);
                detalle.setStock(p.getStock()); // se guarda el stock actual del producto

                // Insertar detalle en base
                boolean okDetalle = invoiceService.insertarDetalle(detalle);

                // Reducir el stock del producto
                boolean okStock = invoiceService.reducirStock(productoId, cantidad);

                if (!okDetalle || !okStock) {
                    todoBien = false;
                    break;
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
