package servlet;

import org.json.JSONObject;
import services.BetService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.sql.SQLException;

import static services.ServiceTools.serviceKO;

@WebServlet(
        name = "AddBetServlet",
        urlPatterns = {"/addBet"}
)
public class AddBetServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject j = ValidatorHelper.getJSONParameter(req,resp);
        resp.setContentType( "text / plain" );
        PrintWriter out = resp.getWriter();

        if( j != null) {
            try {
                String login = ValidatorHelper.getParam(j, "login", true);
                String idPool = ValidatorHelper.getParam(j, "idPool", true);
                String ammount = ValidatorHelper.getParam(j, "betAmmount", true);
                String value = ValidatorHelper.getParam(j, "betValue", true);
                String token = ValidatorHelper.getParam(j, "token", true);
                if(ValidatorHelper.isInteger(ammount) && ValidatorHelper.isDouble(value))
                    out.print(BetService.addBet(token, login, idPool, ammount, value));
                else
                    out.print(serviceKO("Ammount is not a valid Integer"));
            } catch (ValidationException e) {
                out.print(serviceKO(e.getMessage()));
            }
        }else{
            out.print(serviceKO("Aucun arguments"));
        }
        out.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        /*resp.setContentType( "text / plain" );
        PrintWriter out = resp.getWriter();

        try {
            String login = ValidatorHelper.getParam(req, "login", true);
            String idPool = ValidatorHelper.getParam(req, "idPool", true);
            String ammount = ValidatorHelper.getParam(req, "betAmmount", true);
            String value = ValidatorHelper.getParam(req, "betValue", true);
            String token = ValidatorHelper.getParam(req, "token", true);
            out.print(BetService.addBet(token, login, idPool, ammount, value));
        }catch(Exception e){
            out.print(serviceKO(e.toString()));
        }
        out.close();*/
    }
}