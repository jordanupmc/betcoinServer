package servlet;

import services.BetService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static services.ServiceTools.serviceKO;

@WebServlet(
        name = "AddBetServlet",
        urlPatterns = {"/addBet"}
)
public class AddBetServlet extends HttpServlet {

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
            String ammount = ValidatorHelper.getParam(req, "betAmmount", true);
            String value = ValidatorHelper.getParam(req, "betValue", true);
            String token = ValidatorHelper.getParam(req, "token", true);


            out.print(
                    BetService.addBet(token, login, idPool, ammount, value)
            );
        }catch(Exception e){
            out.print(serviceKO(e.getMessage()));
        }
        out.close();
    }
}