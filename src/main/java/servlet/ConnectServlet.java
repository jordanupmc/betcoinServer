package servlet;

import org.json.JSONObject;
import services.LoginService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.io.PrintWriter;

import static services.ServiceTools.serviceKO;

@WebServlet(
        name = "ConnectServlet",
        urlPatterns = {"/connect"}
)
public class ConnectServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        PrintWriter out = resp.getWriter();
        JSONObject j = ValidatorHelper.getJSONParameter(req,resp);

        if(j!=null) {
            try {
                String login = ValidatorHelper.getParam(j, "login", true);
                String mdp = ValidatorHelper.getParam(j, "password", true);
                out.print(
                        LoginService.connect(login, mdp)
                );
            }catch(Exception e){
                out.print(serviceKO("Connect Fail: "+e.getMessage()));
            }
        }
        else {
            out.print(serviceKO("ConnectFail : Aucun parametre recu"));
        }
        out.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

       /* resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();


        //Login*Mdp -> token
        try {
            String login = ValidatorHelper.getParam(req, "login", true);
            String mdp = ValidatorHelper.getParam(req, "password", true);

            out.print(
                    LoginService.connect(login, mdp)
            );
        }catch(Exception e){
            out.print(serviceKO(e.getMessage()));
        }

        out.close();*/
    }
}