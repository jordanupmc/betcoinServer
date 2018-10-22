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

@WebServlet(
        name = "QuitPoolServlet",
        urlPatterns = {"/quitPool"}
)
public class QuitPoolServlet extends HttpServlet {

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
            String idPool = ValidatorHelper.getParam(req, "idPool", true);
            String token = ValidatorHelper.getParam(req, "token", true);
            JSONObject json = new JSONObject();

            if (login != null && idPool != null) {
                json = BetPoolService.quitPool(login, idPool, token);
            } else {

                json.put("status", "KO");
                json.put("errMsg", "Missing login or idPool");
            }
            out.print(json);
        }catch(Exception e){
            out.print(e.getMessage());
        }
        out.close();



    }
}
