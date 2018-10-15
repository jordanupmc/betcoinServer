package servlet;

import services.BetPoolService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static services.ServiceTools.serviceKO;

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
        try {

            String login = ValidatorHelper.getParam(req, "login", true);
            String idPool = ValidatorHelper.getParam(req, "idPool", true);
            String token = ValidatorHelper.getParam(req, "token", true);
            String msg = ValidatorHelper.getParam(req, "msg", true);


            out.print(
                    BetPoolService.messagePool(login, idPool, token, msg)
            );
        }catch (Exception e){
            out.print(serviceKO(e.getMessage()));
        }

        out.close();
    }
}