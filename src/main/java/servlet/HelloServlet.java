package servlet;

import bd.Database;

import java.io.IOException;
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
        ServletOutputStream out = resp.getOutputStream();
        out.write("hello jojo".getBytes());
        testReqBD(out);
        out.flush();
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
