package servlet;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.JSONObject;
import services.APIService;
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
            JSONObject result = APIService.getCryptoCurrency(cryptname,devise,fin,debut,1);
            out.print(result);
        }catch(ValidationException e){
            out.print(serviceKO(e.getMessage()));
        }
        out.close();



    }
}
