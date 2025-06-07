package controller; // Define el paquete donde está esta clase

import jakarta.servlet.ServletException; // Importa la excepción ServletException para manejo de errores en Servlets
import jakarta.servlet.annotation.WebServlet; // Importa la anotación para mapear el servlet a una URL específica
import jakarta.servlet.http.*; // Importa clases HTTP necesarias: HttpServlet, HttpServletRequest, HttpServletResponse, HttpSession
import model.Categoria; // Importa la clase modelo Categoria
import model.Producto; // Importa la clase modelo Producto
import services.ProductService; // Importa la capa de servicio que maneja la lógica de productos

import java.io.IOException; // Importa la excepción IOException para manejo de errores de entrada/salida
import java.math.BigDecimal; // Importa BigDecimal para manejar precios con precisión decimal
import java.util.ArrayList; // Importa ArrayList para listas dinámicas
import java.util.List; // Importa la interfaz List para colecciones

// Sección: Definición del Servlet y su URL de acceso
@WebServlet("/addProduct") // Mapea este servlet para responder a solicitudes con la URL relativa "/addProduct"
public class AddProductServlet extends HttpServlet { // Declara la clase que extiende HttpServlet para manejar solicitudes HTTP

    // Sección: Instancia de servicio para manejar la lógica de productos
    private final ProductService productService = new ProductService(); // Instancia privada y final del servicio de productos

    // Sección: Manejo de peticiones GET para mostrar el formulario de alta de producto
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Método que procesa solicitudes GET

        // 1) Verificar que la sesión exista y que el usuario tenga rol ADMIN
        HttpSession session = request.getSession(false); // Obtiene la sesión actual sin crear una nueva si no existe

        // Si no existe sesión o el rol no es "ADMIN"
        if (session == null || !"ADMIN".equals(session.getAttribute("rol"))) {
            // Redirige al listado general de productos (no permite acceso a formulario)
            response.sendRedirect(request.getContextPath() + "/productos");
            return; // Termina la ejecución del método
        }

        // 2) Preparar datos para mostrar el formulario
        List<Categoria> categorias = productService.listarCategorias(); // Obtiene la lista de categorías desde el servicio

        request.setAttribute("categorias", categorias); // Añade las categorías como atributo para que la JSP pueda acceder a ellas

        // Reenvía la petición y respuesta a la página JSP que contiene el formulario de alta
        request.getRequestDispatcher("addProduct.jsp").forward(request, response);
    }

    // Sección: Manejo de peticiones POST para procesar el formulario de alta de producto
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Método que procesa solicitudes POST

        // Validación de sesión y rol ADMIN (igual que en doGet)
        HttpSession session = request.getSession(false); // Obtiene la sesión actual
        if (session == null || !"ADMIN".equals(session.getAttribute("rol"))) {
            // Redirige si no hay sesión o no es admin
            response.sendRedirect(request.getContextPath() + "/productos");
            return; // Termina ejecución
        }

        // Sección: Lectura de parámetros enviados desde el formulario
        String nombreParam = request.getParameter("nombre"); // Nombre del producto ingresado
        String marcaParam  = request.getParameter("marca");   // Marca del producto ingresada
        String precioParam = request.getParameter("precio");  // Precio en texto
        String stockParam  = request.getParameter("stock");   // Stock en texto
        String categoriaParam = request.getParameter("categoriaId"); // ID de categoría en texto

        // Sección: Inicialización de lista para acumular errores de validación
        List<String> errores = new ArrayList<>(); // Lista dinámica para mensajes de error

        // Validación del nombre del producto
        if (nombreParam == null || nombreParam.trim().isEmpty()) { // Si el nombre está vacío o nulo
            errores.add("El nombre del producto es obligatorio."); // Añade mensaje de error
        } else if (nombreParam.trim().length() < 2) { // Si tiene menos de 2 caracteres
            errores.add("El nombre debe tener al menos 2 caracteres."); // Mensaje de error
        }

        // Validación de la marca del producto
        if (marcaParam == null || marcaParam.trim().isEmpty()) { // Si marca está vacía o nula
            errores.add("La marca del producto es obligatoria."); // Añade error
        }

        // Validación del precio
        BigDecimal precio = null; // Variable para almacenar el precio convertido correctamente
        if (precioParam == null || precioParam.trim().isEmpty()) { // Si falta precio
            errores.add("El precio es obligatorio."); // Mensaje de error
        } else {
            try {
                precio = new BigDecimal(precioParam.trim()); // Convierte cadena a BigDecimal para manejo correcto de decimales
                if (precio.compareTo(BigDecimal.ZERO) <= 0) { // Si el precio es menor o igual a cero
                    errores.add("El precio debe ser un número mayor que 0."); // Mensaje de error
                }
            } catch (NumberFormatException ex) { // Si la conversión falla por formato incorrecto
                errores.add("El precio debe ser un número válido (p. ej. 12.50)."); // Mensaje de error
            }
        }

        // Validación del stock
        Integer stock = null; // Variable para almacenar el stock convertido a entero
        if (stockParam == null || stockParam.trim().isEmpty()) { // Si falta o está vacío
            errores.add("El campo stock es obligatorio."); // Mensaje de error
        } else {
            try {
                stock = Integer.parseInt(stockParam.trim()); // Convierte texto a entero
                if (stock < 0) { // Si el stock es negativo
                    errores.add("El stock no puede ser negativo."); // Mensaje de error
                }
            } catch (NumberFormatException ex) { // Si no se puede convertir a entero
                errores.add("El stock debe ser un número entero (p. ej. 5)."); // Mensaje de error
            }
        }

        // Validación de la categoría
        Integer categoriaId = null; // Variable para almacenar el ID convertido a entero
        try {
            categoriaId = Integer.parseInt(categoriaParam); // Convierte el ID de categoría a entero
        } catch (Exception e) { // Si falla la conversión (parámetro inválido)
            errores.add("Debe seleccionar una categoría válida."); // Mensaje de error
        }

        // Sección: Si existen errores, devolver al formulario con mensajes y datos ingresados
        if (!errores.isEmpty()) { // Si la lista de errores NO está vacía
            request.setAttribute("errores", errores); // Añade los errores para mostrar en la JSP
            request.setAttribute("nombre", nombreParam); // Conserva el valor ingresado en nombre
            request.setAttribute("marca", marcaParam);   // Conserva el valor ingresado en marca
            request.setAttribute("precio", precioParam); // Conserva el precio original como texto
            request.setAttribute("stock", stockParam);   // Conserva el stock original como texto

            // Reenvía al formulario para que el usuario corrija errores
            request.getRequestDispatcher("addProduct.jsp").forward(request, response);
            return; // Termina la ejecución para no continuar con inserción
        }

        // Sección: Crear el objeto Producto con los datos validados
        Producto p = new Producto(); // Instancia un nuevo objeto Producto
        p.setNombre(nombreParam.trim()); // Asigna nombre limpiando espacios en blanco
        p.setMarca(marcaParam.trim());   // Asigna marca limpiando espacios
        p.setPrecio(precio);             // Asigna precio validado y convertido
        p.setStock(stock);               // Asigna stock validado y convertido

        // Sección: Asignar la categoría al producto
        Categoria categoria = new Categoria(); // Crea un objeto Categoria
        categoria.setId(categoriaId);           // Asigna el ID de la categoría seleccionado
        p.setCategoria(categoria);               // Asocia la categoría al producto

        // Sección: Insertar el producto usando el servicio
        productService.insertarProducto(p); // Llama al servicio para persistir el producto en la base de datos

        // Redirigir al listado de productos para ver el nuevo producto agregado
        response.sendRedirect(request.getContextPath() + "/productos");
    }

    /*
     * IMPORTANTE:
     * Si se modifica la validación en doPost, puede permitir datos inválidos que rompan la lógica
     * o la integridad de la base de datos.
     *
     * Si se elimina o modifica la comprobación de sesión y rol ADMIN en doGet/doPost,
     * usuarios no autorizados podrían acceder o modificar productos.
     *
     * Cambiar productService.insertarProducto afectará cómo se guarda el producto, pudiendo
     * generar errores o inconsistencias si no se maneja correctamente.
     */
}
