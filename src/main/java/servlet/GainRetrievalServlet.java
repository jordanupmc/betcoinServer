package servlet;


import org.json.JSONObject;
import services.BetService;

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
        urlPatterns = {"/retrieve"}

)
public class GainRetrievalServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text / plain");
        PrintWriter out = resp.getWriter();
        JSONObject j = ValidatorHelper.getJSONParameter(req,resp);


        try {
            String login = ValidatorHelper.getParam(j, "login", true);
            String idPool = ValidatorHelper.getParam(j, "idPool", true);

            out.print(BetService.retrieveGain(login, idPool));

        }catch(Exception e){
            out.print(serviceKO(e.getMessage()));
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


            JSONObject json ;

            if ((login != null) && (idPool != null)) {
                json = BetService.retrieveGain(login, idPool);
            } else {
                json = serviceKO("Gain Retrieval Failed : Missing login or IdPool");
            }
            out.print(json);

        }catch(Exception e){
            out.print(serviceKO(e.getMessage()));
        }
        out.close();*/

    }
}
