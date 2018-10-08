package servlet;

import bd.Database;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
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
import javax.xml.crypto.Data;

@WebServlet(
        name = "HelloServlet",
        urlPatterns = {"/hello"}
)
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType( "text / plain" );
        PrintWriter out = resp.getWriter();
        out.println("hello JOJO");
        /*testReqBD(out);*/
        testMongo(out);
        String login=req.getParameter("login");
        String mdp=req.getParameter("password");

        if(login != null && mdp!= null) {
            out.println("ou plutot.... hello  "+login);

            out.print(
                    UserService.subscribe(login, mdp, "dupond", "aignan", "bob@etu.fac.fr", Date.valueOf("1988-3-10"), "FRANCE")
            );
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
