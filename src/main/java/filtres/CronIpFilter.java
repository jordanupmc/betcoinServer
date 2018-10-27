package filtres;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
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

        HttpServletRequest httprequest = (HttpServletRequest) request;

        String ip = getClientIp(httprequest);

        HttpServletResponse httpResp = null;

        if (response instanceof HttpServletResponse)
            httpResp = (HttpServletResponse) response;

        System.out.println(ip);

        if (!IP_CRON.equals(ip)) {
            httpResp.sendError(HttpServletResponse.SC_FORBIDDEN,"Access Forbidden");
        } else {

            filterChain.doFilter(request, response);
        }
    }

    private static String getClientIp(HttpServletRequest request) {

        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }

        return remoteAddr;
    }

    @Override
    public void destroy() {

    }
}
