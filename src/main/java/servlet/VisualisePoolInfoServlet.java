package servlet;

import org.json.JSONObject;
import services.AccountService;
import services.BetPoolService;

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
        name = "VisualisePoolInfoServlet",
        urlPatterns = {"/getPoolInfo"}
)
public class VisualisePoolInfoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        try {
            resp.setContentType("text / plain");

            String idPool = ValidatorHelper.getParam(req, "idPool", true);

            out.print(BetPoolService.visualisePool(idPool));

        }catch(ValidationException e){
            out.print(serviceKO(e.getMessage()));
        }
        out.close();


    }
}
