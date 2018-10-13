package servlet;

import services.CancelBetService;
import services.LoginService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(
        name = "CancelBetServlet",
        urlPatterns = {"/cancelBet"}
)
public class CancelBetServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType( "text / plain" );
        PrintWriter out = resp.getWriter();


        String login=req.getParameter("login");
        String idPool=req.getParameter("idPool");
        String token=req.getParameter("token");


        out.print(
                CancelBetService.cancelBet(login, idPool, token)
        );

        out.close();
    }
}