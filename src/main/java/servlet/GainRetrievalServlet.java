package servlet;


import org.json.JSONObject;
import services.BetService;

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
        name = "GainRetrievalServlet",
        urlPatterns = {"/retrieve"}

)
public class GainRetrievalServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text / plain");
        PrintWriter out = resp.getWriter();
        JSONObject j = ValidatorHelper.getJSONParameter(req,resp);

        if(j != null) {
            try {
                String login = ValidatorHelper.getParam(j, "login", true);
                String token = ValidatorHelper.getParam(j, "token", true);
                String idPool = ValidatorHelper.getParam(j, "idPool", true);

                out.print(BetService.retrieveGain(login, token, idPool));

            } catch (Exception e) {
                out.print(serviceKO("GainRetrieval Fail : " + e.getMessage()));
            }
        }else{
            out.print(serviceKO("GainRetrieval Fail"));

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


            JSONObject json ;

            json = BetService.retrieveGain(login,token, idPool);

            out.print(json);

        }catch(ValidationException e){
            out.print(serviceKO("Gain Retrieval Failed : "+e.getMessage()));
        }
        out.close();*/

    }
}
