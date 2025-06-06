package controller; // Define el paquete donde se ubica este servlet

import jakarta.servlet.ServletException; // Excepción lanzada por errores en el servlet
import jakarta.servlet.annotation.WebServlet; // Anotación para mapear el servlet a una URL
import jakarta.servlet.http.*; // Clases HTTP: HttpServlet, HttpServletRequest, HttpServletResponse, HttpSession
import model.Categoria; // Modelo de categoría de producto
import model.Producto; // Modelo de producto
import services.ProductService; // Servicio que maneja la lógica de negocio de productos

import java.io.IOException; // Excepción de I/O
import java.math.BigDecimal; // Para manejo de precios con precisión decimal
import java.util.ArrayList; // Lista dinámica
import java.util.List; // Interfaz de lista

@WebServlet("/editProduct") // Mapea este servlet a la URL “/editProduct”
public class EditProductServlet extends HttpServlet { // Clase que extiende HttpServlet
    private final ProductService productService = new ProductService(); // Instancia el servicio de productos

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Maneja peticiones GET
        HttpSession session = request.getSession(false); // Obtiene la sesión existente sin crear nueva
        if (session == null || !"ADMIN".equals(session.getAttribute("rol"))) { // Verifica que exista sesión y rol ADMIN
            response.sendRedirect(request.getContextPath() + "/productos"); // Redirige si no es ADMIN
            return; // Finaliza método
        }

        String idParam = request.getParameter("id"); // Obtiene parámetro "id" de la URL
        if (idParam == null || idParam.trim().isEmpty()) { // Verifica que el parámetro no esté vacío
            response.sendRedirect(request.getContextPath() + "/productos"); // Redirige si falta id
            return; // Finaliza método
        }

        int id; // Variable para el ID convertido
        try {
            id = Integer.parseInt(idParam.trim()); // Convierte id a entero
        } catch (NumberFormatException e) { // Manejo de formato inválido
            response.sendRedirect(request.getContextPath() + "/productos"); // Redirige si no es número
            return; // Finaliza método
        }

        Producto producto = productService.obtenerPorId(id); // Obtiene producto por ID
        if (producto == null) { // Si no existe el producto
            response.sendRedirect(request.getContextPath() + "/productos"); // Redirige a lista de productos
            return; // Finaliza método
        }

        List<Categoria> categorias = productService.listarCategorias(); // Lista todas las categorías
        request.setAttribute("producto", producto); // Pone el producto en atributos de petición
        request.setAttribute("categorias", categorias); // Pone las categorías en atributos de petición
        request.getRequestDispatcher("editProduct.jsp").forward(request, response); // Forward a la vista de edición
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Maneja peticiones POST
        HttpSession session = request.getSession(false); // Obtiene sesión existente
        if (session == null || !"ADMIN".equals(session.getAttribute("rol"))) { // Verifica rol ADMIN
            response.sendRedirect(request.getContextPath() + "/productos"); // Redirige si no es ADMIN
            return; // Finaliza método
        }

        List<String> errores = new ArrayList<>(); // Lista para almacenar mensajes de error

        String idParam = request.getParameter("id"); // Parámetro id
        String nombreParam = request.getParameter("nombre"); // Parámetro nombre
        String marcaParam = request.getParameter("marca"); // Parámetro marca
        String precioParam = request.getParameter("precio"); // Parámetro precio
        String stockParam = request.getParameter("stock"); // Parámetro stock
        String categoriaParam = request.getParameter("categoriaId"); // Parámetro categoría

        Integer id = null; // Variable para ID convertido
        try {
            id = Integer.parseInt(idParam); // Convierte id a entero
        } catch (Exception e) {
            errores.add("ID de producto inválido."); // Error si no es número válido
        }

        Producto original = null; // Guardará producto original
        if (id != null) { // Si id es válido
            original = productService.obtenerPorId(id); // Obtiene producto original por ID
            if (original == null) { // Si no existe producto
                errores.add("El producto no existe."); // Error de producto inexistente
            }
        }

        // Validar nombre
        if (nombreParam == null || nombreParam.trim().isEmpty()) { // Si falta nombre
            errores.add("El nombre del producto es obligatorio.");
        } else if (nombreParam.trim().length() < 2) { // Si nombre muy corto
            errores.add("El nombre debe tener al menos 2 caracteres.");
        }

        // Validar marca
        if (marcaParam == null || marcaParam.trim().isEmpty()) { // Si falta marca
            errores.add("La marca del producto es obligatoria.");
        }

        // Validar precio
        BigDecimal precio = null; // Variable para precio convertido
        if (precioParam == null || precioParam.trim().isEmpty()) { // Si falta precio
            errores.add("El precio es obligatorio.");
        } else {
            try {
                precio = new BigDecimal(precioParam.trim()); // Convierte precio a BigDecimal
                if (precio.compareTo(BigDecimal.ZERO) <= 0) { // Si no es mayor que cero
                    errores.add("El precio debe ser mayor que 0.");
                }
            } catch (NumberFormatException e) {
                errores.add("Precio inválido."); // Si no se puede parsear
            }
        }

        // Validar stock
        Integer stock = null; // Variable para stock convertido
        if (stockParam == null || stockParam.trim().isEmpty()) { // Si falta stock
            errores.add("El stock es obligatorio.");
        } else {
            try {
                stock = Integer.parseInt(stockParam.trim()); // Convierte stock a entero
                if (stock < 0) { // Si es negativo
                    errores.add("El stock no puede ser negativo.");
                }
            } catch (NumberFormatException e) {
                errores.add("Stock inválido."); // Si no se puede parsear
            }
        }

        // Validar categoría
        Integer categoriaId = null; // Variable para categoría convertida
        try {
            categoriaId = Integer.parseInt(categoriaParam); // Convierte categoría a entero
        } catch (Exception e) {
            errores.add("Debe seleccionar una categoría válida.");
        }

        // Validación de cambios reales
        if (errores.isEmpty() && original != null) { // Si no hay errores y existe el original
            boolean sinCambios =
                    nombreParam.trim().equals(original.getNombre()) && // Nombre igual
                            marcaParam.trim().equals(original.getMarca()) && // Marca igual
                            precio != null && precio.compareTo(original.getPrecio()) == 0 && // Precio igual
                            stock != null && stock.equals(original.getStock()) && // Stock igual
                            categoriaId != null && categoriaId.equals(original.getCategoria().getId()); // Categoría igual

            if (sinCambios) {
                errores.add("No se realizaron cambios. Modifique algún campo para actualizar."); // Error si no cambió nada
            }
        }

        if (!errores.isEmpty()) { // Si hay errores
            Producto prod = new Producto(); // Crea un producto temporal para repoblar formulario
            prod.setId(id); // Asigna id
            prod.setNombre(nombreParam); // Asigna nombre ingresado
            prod.setMarca(marcaParam); // Asigna marca ingresada
            prod.setPrecio(precio); // Asigna precio ingresado
            prod.setStock(stock); // Asigna stock ingresado
            if (categoriaId != null) { // Si categoría válida
                Categoria cat = new Categoria(); // Crea categoría temporal
                cat.setId(categoriaId); // Asigna id de categoría
                prod.setCategoria(cat); // Asocia categoría al producto temporal
            }

            request.setAttribute("producto", prod); // Pone el producto temporal en atributos
            request.setAttribute("errores", errores); // Pone los errores en atributos
            request.setAttribute("categorias", productService.listarCategorias()); // Pone las categorías en atributos
            request.getRequestDispatcher("editProduct.jsp").forward(request, response); // Forward a la vista de edición
            return; // Finaliza método
        }

        // Actualizar producto
        Producto actualizado = new Producto(); // Crea instancia para datos actualizados
        actualizado.setId(id); // Asigna id
        actualizado.setNombre(nombreParam.trim()); // Asigna nombre sin espacios
        actualizado.setMarca(marcaParam.trim()); // Asigna marca sin espacios
        actualizado.setPrecio(precio); // Asigna precio validado
        actualizado.setStock(stock); // Asigna stock validado

        Categoria categoria = new Categoria(); // Crea categoría para asociar
        categoria.setId(categoriaId); // Asigna id de categoría
        actualizado.setCategoria(categoria); // Asocia categoría al producto actualizado

        productService.actualizarProducto(actualizado); // Llama al servicio para guardar cambios
        response.sendRedirect(request.getContextPath() + "/productos"); // Redirige a lista de productos
    }
}
