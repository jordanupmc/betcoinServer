package servlet;

import org.json.JSONObject;
import services.LoginService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static services.ServiceTools.serviceKO;

@WebServlet(
        name = "DisconnectServlet",
        urlPatterns = {"/disconnect"}
)
public class DisconnectServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        JSONObject j = ValidatorHelper.getJSONParameter(req,resp);

        if(j!=null) {
            try {
                String login = ValidatorHelper.getParam(j, "login", true);
                String token = ValidatorHelper.getParam(j, "token", true);
                out.print(
                        LoginService.disconnect(login, token)
                );
            }catch(Exception e){
                out.print(serviceKO(e.getMessage()));
            }
        }
        else {
            out.print(serviceKO("Aucune parametre recu"));
        }
        out.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        /*resp.setContentType("text / plain");
        PrintWriter out = resp.getWriter();
        try {
            String login = ValidatorHelper.getParam(req, "login", true);
            String token = ValidatorHelper.getParam(req, "token", true);

            if (login != null && token != null) {
                JSONObject tmp = LoginService.disconnect(login, token);
                out.print(tmp);
            } else {
                JSONObject json = new JSONObject();
                json.put("status", "KO");
                json.put("errMsg", "Missing login or token");
                out.print(json);
            }
        }catch(Exception e){
            out.print(serviceKO(e.getMessage()));
        }
        out.close();*/



    }
}
