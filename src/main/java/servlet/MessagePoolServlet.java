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
import java.net.URLDecoder;

import static services.ServiceTools.serviceKO;

@WebServlet(
        name = "MessagePoolServlet",
        urlPatterns = {"/messagePool"}
)
public class MessagePoolServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject j = ValidatorHelper.getJSONParameter(req,resp);
        resp.setContentType( "text / plain" );
        PrintWriter out = resp.getWriter();

        if(j != null) {
            try {
                String login = ValidatorHelper.getParam(j, "login", true);
                String idPool = ValidatorHelper.getParam(j, "idPool", true);
                String token = ValidatorHelper.getParam(j, "token", true);
                String msg = ValidatorHelper.getParam(j, "msg", true);
                msg = URLDecoder.decode(msg,"utf-8");

                out.print(
                        BetPoolService.messagePool(login, idPool, token, msg)
                );
            } catch (Exception e) {
                out.print(serviceKO("MessagePool Fail"));
            }
        }else{
            out.print(serviceKO("MessagePool Fail"));

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
            String token = ValidatorHelper.getParam(req, "token", true);
            String msg = ValidatorHelper.getParam(req, "msg", true);


            out.print(
                    BetPoolService.messagePool(login, idPool, token, msg)
            );
        }catch (Exception e){
            out.print(serviceKO(e.getMessage()));
        }

        out.close();*/
    }
}