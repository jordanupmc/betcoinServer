package servlet;

import org.json.JSONObject;
import services.ServiceTools;
import services.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(
        name = "UnsubscribeServlet",
        urlPatterns = {"/unsubscribe"}
)
public class UnsubscribeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject j = ValidatorHelper.getJSONParameter(req,resp);
        resp.setContentType( "text / plain" );
        PrintWriter out = resp.getWriter();

        try {
            String login = ValidatorHelper.getParam(j, "login", true);
            String token = ValidatorHelper.getParam(j, "token", true);
            String password = ValidatorHelper.getParam(j, "password", true);


            out.println(UserService.unsubscribe(login, token, password));

        }catch (ValidationException ve){
            out.println(ServiceTools.serviceKO( "Unsubscribe fail "+ve.getMessage()));
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
            String token = ValidatorHelper.getParam(req, "token", true);
            String password = ValidatorHelper.getParam(req, "password", true);


            out.println(UserService.unsubscribe(login, token, password));

        }catch (ValidationException ve){
            out.println(ServiceTools.serviceKO( "Unsubscribe fail "+ve.getMessage()));
        }
        out.close();*/
    }
}
