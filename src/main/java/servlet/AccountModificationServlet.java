package servlet;

import bd.Database;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONObject;
import services.BetPoolService;
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
        name = "AccountModificationServlet",
        urlPatterns = {"/changeAccountInfo"}
)
public class AccountModificationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text / plain");
        PrintWriter out = resp.getWriter();

        String login = req.getParameter("login");
        String pwd = req.getParameter("password");
        String field_name = req.getParameter("fieldname");
        String new_value = req.getParameter("newvalue");
        String token = req.getParameter("token");

        JSONObject json = new JSONObject();

        if((login != null)&&(token!=null)&&(pwd!=null)&&(field_name!=null)&&(new_value!=null) ){
            json = UserService.changeFieldAccount(login,pwd,field_name,new_value,token);
        }else{
            serviceKO("Missing login");
        }
        out.print(json);
        out.close();



    }
}
