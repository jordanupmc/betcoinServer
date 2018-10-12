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
import javax.xml.bind.ValidationException;

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

        try {
            String login = ValidatorHelper.getParam(req, "login", true);
            String mdp = ValidatorHelper.getParam(req, "password", true);
            String cmdp = ValidatorHelper.getParam(req, "confirmPassword", true);
            String email = ValidatorHelper.getParam(req, "email", true);
            String nom = ValidatorHelper.getParam(req, "lastName", true);
            String prenom = ValidatorHelper.getParam(req, "firstName", true);
            String dateNaiss = ValidatorHelper.getParam(req, "dateNaiss", true);
            String pays = ValidatorHelper.getParam(req, "country", true);

            if(login != null && mdp!= null && cmdp != null && email!= null && nom != null && prenom!= null && dateNaiss != null && pays!= null
                && ValidatorHelper.isEmail(email)
            ) {
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
        }catch (ValidationException ve){

            JSONObject j =new JSONObject();
            j.put("status", "KO");
            j.put("errorMsg", ve.getMessage());
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
