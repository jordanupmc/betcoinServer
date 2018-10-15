package servlet;

import services.BetPoolService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(
        name = "MessagePoolServlet",
        urlPatterns = {"/messagePool"}
)
public class MessagePoolServlet extends HttpServlet {

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
        String msg=req.getParameter("message");



        out.print(
                BetPoolService.messagePool(login, idPool, token, msg)
        );

        out.close();
    }
}