package servlet;

import org.json.JSONObject;
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
        name = "QuitPoolServlet",
        urlPatterns = {"/quitPool"}
)
public class QuitPoolServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject j = ValidatorHelper.getJSONParameter(req,resp);
        resp.setContentType("text / plain");
        PrintWriter out = resp.getWriter();

        if(j != null) {
            try {
                String login = ValidatorHelper.getParam(j, "login", true);
                String idPool = ValidatorHelper.getParam(j, "idPool", true);
                String token = ValidatorHelper.getParam(j, "token", true);

                out.print(BetPoolService.quitPool(login, idPool, token));
            } catch (Exception e) {
                out.print(serviceKO("QuitPool Failed : " + e.toString()));
            }
        }else{
            out.print(serviceKO("QuitPool Failed : Aucun parametre recu"));
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
            String idPool = ValidatorHelper.getParam(req, "idPool", true);
            String token = ValidatorHelper.getParam(req, "token", true);
            JSONObject json = new JSONObject();

            if (login != null && idPool != null) {
                json = BetPoolService.quitPool(login, idPool, token);
            } else {
                json = serviceKO("QuitPool Failed : Missing login or idPool");
            }
            out.print(json);
        }catch(Exception e){
            out.print(serviceKO("QuitPool Failed : "+e.toString()));
        }
        out.close();*/



    }
}
