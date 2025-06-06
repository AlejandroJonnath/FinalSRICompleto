package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Categoria;
import model.Producto;
import services.ProductService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/addProduct")
public class AddProductServlet extends HttpServlet {
    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1) Verificar que la sesión exista y sea rol ADMIN
        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("rol"))) {
            response.sendRedirect(request.getContextPath() + "/productos");
            return;
        }

        // 2) Simplemente mostramos el formulario
        List<Categoria> categorias = productService.listarCategorias();
        request.setAttribute("categorias", categorias);
        request.getRequestDispatcher("addProduct.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("rol"))) {
            response.sendRedirect(request.getContextPath() + "/productos");
            return;
        }

        String nombreParam = request.getParameter("nombre");
        String marcaParam  = request.getParameter("marca");
        String precioParam = request.getParameter("precio");
        String stockParam  = request.getParameter("stock");
        String categoriaParam = request.getParameter("categoriaId");

        List<String> errores = new ArrayList<>();

        if (nombreParam == null || nombreParam.trim().isEmpty()) {
            errores.add("El nombre del producto es obligatorio.");
        } else if (nombreParam.trim().length() < 2) {
            errores.add("El nombre debe tener al menos 2 caracteres.");
        }

        if (marcaParam == null || marcaParam.trim().isEmpty()) {
            errores.add("La marca del producto es obligatoria.");
        }

        BigDecimal precio = null;
        if (precioParam == null || precioParam.trim().isEmpty()) {
            errores.add("El precio es obligatorio.");
        } else {
            try {
                precio = new BigDecimal(precioParam.trim());
                if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                    errores.add("El precio debe ser un número mayor que 0.");
                }
            } catch (NumberFormatException ex) {
                errores.add("El precio debe ser un número válido (p. ej. 12.50).");
            }
        }

        Integer stock = null;
        if (stockParam == null || stockParam.trim().isEmpty()) {
            errores.add("El campo stock es obligatorio.");
        } else {
            try {
                stock = Integer.parseInt(stockParam.trim());
                if (stock < 0) {
                    errores.add("El stock no puede ser negativo.");
                }
            } catch (NumberFormatException ex) {
                errores.add("El stock debe ser un número entero (p. ej. 5).");
            }
        }

        Integer categoriaId = null;
        try {
            categoriaId = Integer.parseInt(categoriaParam);
        } catch (Exception e) {
            errores.add("Debe seleccionar una categoría válida.");
        }

        if (!errores.isEmpty()) {
            request.setAttribute("errores", errores);
            request.setAttribute("nombre", nombreParam);
            request.setAttribute("marca", marcaParam);
            request.setAttribute("precio", precioParam);
            request.setAttribute("stock", stockParam);
            request.getRequestDispatcher("addProduct.jsp").forward(request, response);
            return;
        }

        Producto p = new Producto();
        p.setNombre(nombreParam.trim());
        p.setMarca(marcaParam.trim());
        p.setPrecio(precio);
        p.setStock(stock);

        // ⚠️ ESTA ES LA PARTE QUE FALTABA
        Categoria categoria = new Categoria();
        categoria.setId(categoriaId);
        p.setCategoria(categoria);

        productService.insertarProducto(p);

        response.sendRedirect(request.getContextPath() + "/productos");
    }
}