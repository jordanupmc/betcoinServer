package servlet;

import bd.Database;
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
        name = "HelloServlet",
        urlPatterns = {"/hello"}
)
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType( "text / plain" );
        PrintWriter out = resp.getWriter();
        out.println("hello jojo");
        /*testReqBD(out);*/
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

    private void testReqBD(ServletOutputStream out){
        try {
            Connection c =  Database.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT * FROM Jojo_Currency");
            ResultSet resultSet = ps.executeQuery();
            out.println("\n------SQL----------");
            if(resultSet.next()){
                out.println("\n"+resultSet.getString(1));
                out.println(resultSet.getDouble(2));
            }
            resultSet.close();
            ps.close();
            c.close();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
