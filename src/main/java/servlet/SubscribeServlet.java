package servlet;

import bd.Database;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONObject;
import services.UserService;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
        name = "SubscribeServlet",
        urlPatterns = {"/subscribe"}
)
public class SubscribeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType( "text / plain" );
        PrintWriter out = resp.getWriter();


        //Login*Mdp*ConfirmMdp*Email*Nom*Prenom*DateNaissance(dd/mm/aaaa)*Pays->void

        String login=req.getParameter("login");
        String mdp=req.getParameter("password");
        String cmdp=req.getParameter("confirmPassword");
        String email=req.getParameter("email");
        String nom=req.getParameter("lastName");
        String prenom=req.getParameter("firstName");
        String dateNaiss=req.getParameter("dateNaiss");
        String pays=req.getParameter("country");

        if(login != null && mdp!= null && cmdp != null && email!= null && nom != null && prenom!= null && dateNaiss != null && pays!= null) {
            out.print(
                    UserService.subscribe(login, mdp, cmdp,email, nom, prenom, Date.valueOf(dateNaiss), pays)
            );
        }
        else {
            JSONObject j =new JSONObject();
            j.put("status", "KO");
            j.put("errorMsg", "Subscribe fail error param");
            out.print(j);
        }
        out.close();
    }


    private void testMongo(PrintWriter out){
        MongoClientURI uri  = new MongoClientURI(Database.mongoURI);
        MongoClient client = new MongoClient(uri);
        MongoDatabase db = client.getDatabase(uri.getDatabase());
        MongoCollection<Document> v = db.getCollection("dummy");
        Document d = v.find().first();
        out.println(d.toJson());
        client.close();
    }


}
