package filtres;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(
        filterName="CronIpFilter",
        urlPatterns = {"/createPool"}
        )
public class CronIpFilter implements Filter {

    private static final String IP_CRON = "195.201.26.157";
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    /*On limite l'acces a la servlet createPool*/
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        String ip = request.getRemoteAddr();

        HttpServletResponse httpResp = null;

        if (response instanceof HttpServletResponse)
            httpResp = (HttpServletResponse) response;

        if (!IP_CRON.equals(ip)) {
            httpResp.sendError(HttpServletResponse.SC_FORBIDDEN,"Access Forbidden");
        } else {

            filterChain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
