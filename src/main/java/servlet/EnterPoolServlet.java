package servlet;

import services.EnterPoolService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(
        name = "EnterPoolServlet",
        urlPatterns = {"/enterPool"}
)
public class EnterPoolServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType( "text / plain" );
        PrintWriter out = resp.getWriter();


        //Login*Mdp -> token

        String login=req.getParameter("login");
        String idPool=req.getParameter("idPool");

        out.print(
                EnterPoolService.enterPool(login, idPool)
        );

        out.close();
    }
}