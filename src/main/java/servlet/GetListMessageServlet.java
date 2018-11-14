package servlet;

import bd.CryptoEnum;
import org.json.JSONObject;
import services.BetPoolService;
import services.ServiceTools;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.ValidationException;
import javax.xml.ws.Service;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet(
        name = "GetListMessageServlet",
        urlPatterns = {"/getListMessage"}
)
public class GetListMessageServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType( "text / plain" );
        PrintWriter out = resp.getWriter();

        try {
            String idPool = ValidatorHelper.getParam(req, "idPool", true);
            String login = ValidatorHelper.getParam(req, "login", true);
            String token = ValidatorHelper.getParam(req, "token", true);
            String from = ValidatorHelper.getParam(req, "from", false);
            String to = ValidatorHelper.getParam(req, "to", false);
            if(ValidatorHelper.isInteger(idPool) && from == null) {
                out.println(BetPoolService.getListMessagePool(login, token, Integer.parseInt(idPool)));
            }
            else if(ValidatorHelper.isInteger(idPool)){
                out.println(BetPoolService.getListMessagePool(login, token, Integer.parseInt(idPool), from));
            }
            else{
                out.print(
                        ServiceTools.serviceKO(idPool+" not a valid idPool")
                );
            }
        }catch(ValidationException e){
            out.print(
                    ServiceTools.serviceKO(e.getMessage())
            );
        }

        out.close();
    }
}