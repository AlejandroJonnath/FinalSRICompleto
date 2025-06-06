package filtro; // Paquete donde se ubica el filtro de autenticación

import jakarta.servlet.*; // Importa clases de Servlet API: Filter, FilterChain, ServletRequest, ServletResponse, etc.
import jakarta.servlet.annotation.WebFilter; // Importa la anotación para declarar un filtro
import jakarta.servlet.http.*; // Importa clases HTTP: HttpServletRequest, HttpServletResponse, HttpSession

import java.io.IOException; // Importa excepción de I/O

@WebFilter("/*") // Aplica este filtro a todas las rutas de la aplicación
public class AuthFilter implements Filter { // Clase que implementa la interfaz Filter

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { } // Inicialización del filtro (vacía)

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException { // Método que intercepta cada petición/response

        HttpServletRequest request = (HttpServletRequest) req; // Cast de ServletRequest a HttpServletRequest
        HttpServletResponse response = (HttpServletResponse) res; // Cast de ServletResponse a HttpServletResponse
        String uri = request.getRequestURI(); // Obtiene la URI de la petición actual
        HttpSession session = request.getSession(false); // Obtiene la sesión existente, sin crear una nueva

        // Determina si la ruta corresponde al login, índice o recursos estáticos
        boolean esLogin = uri.endsWith("login") || uri.endsWith("index.jsp")
                || uri.contains("css/") || uri.contains("js/") || uri.contains("images/");
        // Comprueba si el usuario ya está autenticado en sesión
        boolean yaLogueado = (session != null && session.getAttribute("usuario") != null);

        if (yaLogueado || esLogin) { // Si ya está autenticado o está pidiendo login o recursos públicos
            chain.doFilter(req, res); // Permite continuar con la petición
        } else { // Si no está autenticado y accede a recurso protegido
            response.sendRedirect(request.getContextPath() + "/login"); // Redirige al formulario de login
        }
    }

    @Override
    public void destroy() { } // Limpieza de recursos del filtro (vacía)
}
