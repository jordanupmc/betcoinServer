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
        name = "DeleteMessageServlet",
        urlPatterns = {"/deleteMessage"}
)
public class DeleteMessageServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        JSONObject j = ValidatorHelper.getJSONParameter(req,resp);

        if(j != null) {
            try {
                String login = ValidatorHelper.getParam(j, "login", true);
                String idPool = ValidatorHelper.getParam(j, "idPool", true);
                String token = ValidatorHelper.getParam(j, "token", true);
                String msgId = ValidatorHelper.getParam(j, "msgId", true);

                out.print(
                        BetPoolService.deleteMessage(login, idPool, token, msgId)
                );
            } catch (Exception e) {
                out.print(serviceKO("DeleteMessageServlet Fail :" + e.getMessage()));
            }
        }else{
            out.print(serviceKO("DeleteMessageServlet Fail : Aucun parametre recu"));
        }

        out.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    }
}