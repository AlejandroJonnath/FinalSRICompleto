package filtro; // Paquete donde se ubica el filtro de autenticación

import jakarta.servlet.*; // Importa clases de Servlet API necesarias para filtros y manejo de peticiones
import jakarta.servlet.annotation.WebFilter; // Permite declarar filtros con anotaciones
import jakarta.servlet.http.*; // Clases específicas para manejo HTTP (request, response, sesión)

import java.io.IOException; // Para manejo de excepciones de entrada/salida

@WebFilter("/*") // Declara que este filtro interceptará todas las peticiones (cualquier URL)
public class AuthFilter implements Filter { // Implementa la interfaz Filter para definir un filtro de servlet

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Método de inicialización del filtro, puede usarse para configurar recursos
        // En este caso está vacío porque no se necesita inicialización especial
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        // Método central del filtro, intercepta cada petición antes de llegar al servlet o recurso

        // Convierte los objetos genéricos ServletRequest/ServletResponse a objetos HTTP específicos
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String uri = request.getRequestURI(); // Obtiene la URI (ruta) de la petición actual
        HttpSession session = request.getSession(false);
        // Obtiene la sesión actual si existe, pero no crea una nueva sesión si no hay

        // Define qué URIs no requieren autenticación: página login, index y recursos estáticos (CSS, JS, imágenes)
        boolean esLogin = uri.endsWith("login") || uri.endsWith("index.jsp")
                || uri.contains("css/") || uri.contains("js/") || uri.contains("images/");

        // Verifica si el usuario ya está autenticado en la sesión (atributo "usuario" existe)
        boolean yaLogueado = (session != null && session.getAttribute("usuario") != null);

        if (yaLogueado || esLogin) {
            // Si el usuario está autenticado o la petición es a una ruta pública,
            // se permite continuar la cadena de filtros y la petición hacia su destino
            chain.doFilter(req, res);
        } else {
            // Si no está autenticado y quiere acceder a un recurso protegido,
            // se redirige al formulario de login para autenticarse
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }

    @Override
    public void destroy() {
        // Método para liberar recursos del filtro cuando la aplicación se detiene o el filtro se destruye
        // Vacío aquí porque no hay recursos a liberar
    }
}
