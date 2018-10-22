package servlet;


import org.json.JSONObject;
import services.AccountService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static services.ServiceTools.serviceKO;

@WebServlet(
        name = "GainRetrievalServlet",
        urlPatterns = {"/changeAccountInfo"}
)
public class GainRetrievalServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text / plain");
        PrintWriter out = resp.getWriter();

        try {
            String login = ValidatorHelper.getParam(req, "login", true);
            String pwd = ValidatorHelper.getParam(req, "password", true);
            String field_name = ValidatorHelper.getParam(req, "fieldname", true);
            String new_value = ValidatorHelper.getParam(req, "newvalue", true);
            String token = ValidatorHelper.getParam(req, "token", true);

            JSONObject json = new JSONObject();

            if ((login != null) && (token != null) && (pwd != null) && (field_name != null) && (new_value != null)) {
                json = AccountService.changeFieldAccount(login, pwd, field_name, new_value, token);
            } else {
                json = serviceKO("Missing login");
            }
            out.print(json);

        }catch(Exception e){
            out.print(serviceKO(e.getMessage()));
        }
        out.close();



    }
}
