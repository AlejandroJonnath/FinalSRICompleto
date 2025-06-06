package controller; // Define el paquete donde está esta clase

import jakarta.servlet.ServletException; // Importa la excepción ServletException
import jakarta.servlet.annotation.WebServlet; // Importa la anotación para mapear el servlet a una URL
import jakarta.servlet.http.*; // Importa clases HTTP: HttpServlet, HttpServletRequest, HttpServletResponse y HttpSession
import model.Categoria; // Importa la clase Categoria del modelo
import model.Producto; // Importa la clase Producto del modelo
import services.ProductService; // Importa la capa de servicio que maneja lógica de productos

import java.io.IOException; // Importa la excepción IOException
import java.math.BigDecimal; // Importa BigDecimal para manejar precios con precisión
import java.util.ArrayList; // Importa ArrayList para colecciones dinámicas
import java.util.List; // Importa la interfaz List

@WebServlet("/addProduct") // Mapea este servlet a la URL “/addProduct”
public class AddProductServlet extends HttpServlet { // Declara la clase que extiende HttpServlet
    private final ProductService productService = new ProductService(); // Instancia el servicio de productos

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Método que maneja peticiones GET
        // 1) Verificar que la sesión exista y sea rol ADMIN
        HttpSession session = request.getSession(false); // Obtiene la sesión existente, sin crear una nueva
        if (session == null || !"ADMIN".equals(session.getAttribute("rol"))) { // Si no hay sesión o el rol no es ADMIN
            response.sendRedirect(request.getContextPath() + "/productos"); // Redirige a la lista de productos
            return; // Termina la ejecución del método
        }

        // 2) Simplemente mostramos el formulario
        List<Categoria> categorias = productService.listarCategorias(); // Obtiene la lista de categorías desde el servicio
        request.setAttribute("categorias", categorias); // Añade las categorías como atributo de la petición
        request.getRequestDispatcher("addProduct.jsp").forward(request, response); // Reenvía a la JSP del formulario
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { // Método que maneja peticiones POST
        HttpSession session = request.getSession(false); // Obtiene la sesión existente
        if (session == null || !"ADMIN".equals(session.getAttribute("rol"))) { // Verifica rol ADMIN
            response.sendRedirect(request.getContextPath() + "/productos"); // Redirige si no es ADMIN
            return; // Termina la ejecución
        }

        // Lee parámetros del formulario
        String nombreParam = request.getParameter("nombre"); // Nombre del producto
        String marcaParam  = request.getParameter("marca"); // Marca del producto
        String precioParam = request.getParameter("precio"); // Precio (texto)
        String stockParam  = request.getParameter("stock"); // Stock (texto)
        String categoriaParam = request.getParameter("categoriaId"); // ID de categoría (texto)

        List<String> errores = new ArrayList<>(); // Lista para almacenar mensajes de error

        // Validación del nombre
        if (nombreParam == null || nombreParam.trim().isEmpty()) { // Si falta o está vacío
            errores.add("El nombre del producto es obligatorio."); // Mensaje de error
        } else if (nombreParam.trim().length() < 2) { // Si tiene menos de 2 caracteres
            errores.add("El nombre debe tener al menos 2 caracteres."); // Mensaje de error
        }

        // Validación de la marca
        if (marcaParam == null || marcaParam.trim().isEmpty()) { // Si falta o está vacío
            errores.add("La marca del producto es obligatoria."); // Mensaje de error
        }

        // Validación del precio
        BigDecimal precio = null; // Variable para precio convertido
        if (precioParam == null || precioParam.trim().isEmpty()) { // Si falta o está vacío
            errores.add("El precio es obligatorio."); // Mensaje de error
        } else {
            try {
                precio = new BigDecimal(precioParam.trim()); // Convierte el texto a BigDecimal
                if (precio.compareTo(BigDecimal.ZERO) <= 0) { // Si el precio no es mayor que 0
                    errores.add("El precio debe ser un número mayor que 0."); // Mensaje de error
                }
            } catch (NumberFormatException ex) { // Si falla la conversión
                errores.add("El precio debe ser un número válido (p. ej. 12.50)."); // Mensaje de error
            }
        }

        // Validación del stock
        Integer stock = null; // Variable para stock convertido
        if (stockParam == null || stockParam.trim().isEmpty()) { // Si falta o está vacío
            errores.add("El campo stock es obligatorio."); // Mensaje de error
        } else {
            try {
                stock = Integer.parseInt(stockParam.trim()); // Convierte el texto a entero
                if (stock < 0) { // Si el stock es negativo
                    errores.add("El stock no puede ser negativo."); // Mensaje de error
                }
            } catch (NumberFormatException ex) { // Si falla la conversión
                errores.add("El stock debe ser un número entero (p. ej. 5)."); // Mensaje de error
            }
        }

        // Validación de la categoría
        Integer categoriaId = null; // Variable para categoría convertida
        try {
            categoriaId = Integer.parseInt(categoriaParam); // Convierte el texto a entero
        } catch (Exception e) { // Si falla la conversión
            errores.add("Debe seleccionar una categoría válida."); // Mensaje de error
        }

        // Si hay errores, volvemos al formulario con ellos
        if (!errores.isEmpty()) { // Si la lista de errores no está vacía
            request.setAttribute("errores", errores); // Añade errores como atributo
            request.setAttribute("nombre", nombreParam); // Conserva el nombre ingresado
            request.setAttribute("marca", marcaParam); // Conserva la marca ingresada
            request.setAttribute("precio", precioParam); // Conserva el precio ingresado
            request.setAttribute("stock", stockParam); // Conserva el stock ingresado
            request.getRequestDispatcher("addProduct.jsp").forward(request, response); // Reenvía al formulario
            return; // Termina la ejecución
        }

        // Si no hay errores, creamos el objeto Producto
        Producto p = new Producto(); // Instancia un nuevo producto
        p.setNombre(nombreParam.trim()); // Asigna el nombre sin espacios extras
        p.setMarca(marcaParam.trim()); // Asigna la marca sin espacios extras
        p.setPrecio(precio); // Asigna el precio validado
        p.setStock(stock); // Asigna el stock validado

        // ESTA ES LA PARTE QUE FALTABA
        Categoria categoria = new Categoria(); // Instancia la categoría
        categoria.setId(categoriaId); // Asigna el ID de categoría
        p.setCategoria(categoria); // Asocia la categoría al producto

        productService.insertarProducto(p); // Llama al servicio para guardar el producto

        response.sendRedirect(request.getContextPath() + "/productos"); // Redirige a la lista de productos
    }
}
