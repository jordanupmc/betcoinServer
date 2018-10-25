package servlet;

import services.BetPoolService;
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
        name = "GetListBetsServlet",
        urlPatterns = {"/getListBets"}
)
public class GetListBetsServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text / plain");
        PrintWriter out = resp.getWriter();
        try {
            String login = ValidatorHelper.getParam(req, "login", true);
            String token = ValidatorHelper.getParam(req, "token", true);

            out.print(
                    BetService.getListBets(login, token)
            );
        }catch(Exception e){
            out.print(serviceKO("GetListBets Fail : "+e.toString()));
        }


        out.close();
    }
}