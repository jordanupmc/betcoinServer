package bd;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;

public class UserTools {
    public static boolean subscribe(String login, String mdp, String email, String nom, String prenom, Date birthDate, String country){
        try {
            Connection c = Database.getConnection();
            String query =
                    "INSERT INTO USERS(login,password, last_name, first_name, email, birthday, country)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?);";

            PreparedStatement pstmt = c.prepareStatement(query);
            pstmt.setString(1, login);
            pstmt.setString(2, mdp);
            pstmt.setString(3, nom);
            pstmt.setString(4,prenom);
            pstmt.setString(5,email);
            pstmt.setDate(6,birthDate);
            pstmt.setString(7,country);

            pstmt.executeUpdate();

            pstmt.close();
            c.close();
            return true;
        } catch (Exception E){
            return false;
        }
    }
}
