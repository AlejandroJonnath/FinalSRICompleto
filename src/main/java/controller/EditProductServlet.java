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

@WebServlet("/editProduct")
public class EditProductServlet extends HttpServlet {
    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("rol"))) {
            response.sendRedirect(request.getContextPath() + "/productos");
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/productos");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idParam.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/productos");
            return;
        }

        Producto producto = productService.obtenerPorId(id);
        if (producto == null) {
            response.sendRedirect(request.getContextPath() + "/productos");
            return;
        }

        List<Categoria> categorias = productService.listarCategorias();
        request.setAttribute("producto", producto);
        request.setAttribute("categorias", categorias);
        request.getRequestDispatcher("editProduct.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("rol"))) {
            response.sendRedirect(request.getContextPath() + "/productos");
            return;
        }

        List<String> errores = new ArrayList<>();

        String idParam = request.getParameter("id");
        String nombreParam = request.getParameter("nombre");
        String marcaParam = request.getParameter("marca");
        String precioParam = request.getParameter("precio");
        String stockParam = request.getParameter("stock");
        String categoriaParam = request.getParameter("categoriaId");

        Integer id = null;
        try {
            id = Integer.parseInt(idParam);
        } catch (Exception e) {
            errores.add("ID de producto inválido.");
        }

        Producto original = null;
        if (id != null) {
            original = productService.obtenerPorId(id);
            if (original == null) {
                errores.add("El producto no existe.");
            }
        }

        // Validar campos
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
                    errores.add("El precio debe ser mayor que 0.");
                }
            } catch (NumberFormatException e) {
                errores.add("Precio inválido.");
            }
        }

        Integer stock = null;
        if (stockParam == null || stockParam.trim().isEmpty()) {
            errores.add("El stock es obligatorio.");
        } else {
            try {
                stock = Integer.parseInt(stockParam.trim());
                if (stock < 0) {
                    errores.add("El stock no puede ser negativo.");
                }
            } catch (NumberFormatException e) {
                errores.add("Stock inválido.");
            }
        }

        Integer categoriaId = null;
        try {
            categoriaId = Integer.parseInt(categoriaParam);
        } catch (Exception e) {
            errores.add("Debe seleccionar una categoría válida.");
        }

        // Validación de cambios reales
        if (errores.isEmpty() && original != null) {
            boolean sinCambios =
                    nombreParam.trim().equals(original.getNombre()) &&
                            marcaParam.trim().equals(original.getMarca()) &&
                            precio != null && precio.compareTo(original.getPrecio()) == 0 &&
                            stock != null && stock.equals(original.getStock()) &&
                            categoriaId != null && categoriaId.equals(original.getCategoria().getId());

            if (sinCambios) {
                errores.add("No se realizaron cambios. Modifique algún campo para actualizar.");
            }
        }

        if (!errores.isEmpty()) {
            Producto prod = new Producto();
            prod.setId(id);
            prod.setNombre(nombreParam);
            prod.setMarca(marcaParam);
            prod.setPrecio(precio);
            prod.setStock(stock);
            if (categoriaId != null) {
                Categoria cat = new Categoria();
                cat.setId(categoriaId);
                prod.setCategoria(cat);
            }

            request.setAttribute("producto", prod);
            request.setAttribute("errores", errores);
            request.setAttribute("categorias", productService.listarCategorias());
            request.getRequestDispatcher("editProduct.jsp").forward(request, response);
            return;
        }

        // Actualizar producto
        Producto actualizado = new Producto();
        actualizado.setId(id);
        actualizado.setNombre(nombreParam.trim());
        actualizado.setMarca(marcaParam.trim());
        actualizado.setPrecio(precio);
        actualizado.setStock(stock);

        Categoria categoria = new Categoria();
        categoria.setId(categoriaId);
        actualizado.setCategoria(categoria);

        productService.actualizarProducto(actualizado);
        response.sendRedirect(request.getContextPath() + "/productos");
    }
}
