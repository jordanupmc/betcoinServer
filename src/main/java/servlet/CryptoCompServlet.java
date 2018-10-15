package servlet;



import bd.APITools;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

import static services.ServiceTools.serviceKO;

@WebServlet(
        name = "CryptoCompServlet",
        urlPatterns = {"/getCrypto"}
)
public class CryptoCompServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text / plain");
        PrintWriter out = resp.getWriter();
        try{
            String cryptname = ValidatorHelper.getParam(req,"cryptName",true);
            String devise = ValidatorHelper.getParam(req, "devise",true);
            String fin = ValidatorHelper.getParam(req, "fin",true);
            String debut = ValidatorHelper.getParam(req, "debut", true);
            String result = APITools.getCrypto(cryptname,devise,fin,debut);

        }catch(Exception e){
            out.print(serviceKO(e.getMessage()));
        }
        out.close();



    }
}
