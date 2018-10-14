package servlet;

import org.json.JSONObject;
import services.ServiceTools;
import services.UserService;

import javax.jws.soap.SOAPBinding;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;

@WebServlet(
        name = "UnsubscribeServlet",
        urlPatterns = {"/unsubscribe"}
)
public class UnsubscribeServlet extends HttpServlet {

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
            String token = ValidatorHelper.getParam(req, "token", true);

            if (login != null && token != null) {
                out.println(UserService.unsubscribe(login, token));
            } else {
                out.println(ServiceTools.serviceKO("Unsubscribe fail"));
            }
        }catch (ValidationException ve){
            out.println(ServiceTools.serviceKO( "Unsubscribe fail "+ve.getMessage()));
        }
        out.close();
    }
}
