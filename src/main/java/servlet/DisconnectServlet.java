package servlet;

import bd.Database;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONObject;
import services.LoginService;
import services.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;

import static services.ServiceTools.serviceKO;

@WebServlet(
        name = "DisconnectServlet",
        urlPatterns = {"/disconnect"}
)
public class DisconnectServlet extends HttpServlet {

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
            String token = ValidatorHelper.getParam(req, "token", true);

            if (login != null && token != null) {
                JSONObject tmp = LoginService.disconnect(login, token);
                out.print(tmp);
            } else {
                JSONObject json = new JSONObject();
                json.put("status", "KO");
                json.put("errMsg", "Missing login or token");
                out.print(json);
            }
        }catch(Exception e){
            out.print(serviceKO(e.getMessage()));
        }
        out.close();



    }
}
