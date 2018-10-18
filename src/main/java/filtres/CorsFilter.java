package filtres;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = { "/*" })
public class CorsFilter implements Filter {

    public void destroy() {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        httpResponse.addHeader("Access-Control-Allow-Origin", "*");
        httpResponse.addHeader("Access-Control-Allow-Headers", "content-type");
        httpResponse.addHeader("Access-Control-Allow-Methods","GET, PUT, POST, DELETE");

        httpResponse.setContentType("application/json");

        chain.doFilter(servletRequest, httpResponse);
    }

    public void init(FilterConfig fConfig) throws ServletException {
    }

}