package servlet;

import bd.CryptoEnum;
import services.LoginService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static services.ServiceTools.serviceKO;

@WebServlet(
        name = "CreatePoolServlet",
        urlPatterns = {"/createPool"}
)
public class CreatePoolServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType( "text / plain" );
        PrintWriter out = resp.getWriter();

        try {
            String name = ValidatorHelper.getParam(req, "namePool", true);
            String poolType = ValidatorHelper.getParam(req, "poolType", true);
            String cryptoCurr = ValidatorHelper.getParam(req, "cryptoCurrency", true);

            if(CryptoEnum.contains(cryptoCurr) && ValidatorHelper.checkBoolean(poolType) ) {
                out.print(
                        "CREATE POOL LOG OK: " + bd.PoolTools.createPool(name, CryptoEnum.valueOf(cryptoCurr), Boolean.getBoolean(poolType))
                );
                System.out.println("JE FINIS");
            }
            else
                out.print(
                        "CREATE POOL LOG KO: monnaie inconnus"
                );
        }catch(Exception e){
            out.print(
                    "CREATE POOL LOG KO: "+ e
            );
        }

        out.close();
    }
}