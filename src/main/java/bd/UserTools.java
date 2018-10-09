package bd;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;

public class UserTools {
    public static boolean subscribe(String login, String mdp, String email, String nom, String prenom, Date birthDate, String country){
        String query =
                "INSERT INTO USERS(login,password, last_name, first_name, email, birthday, country)" +
                        "VALUES (?, ?, ?, ?, ?, ?, ?);";
        try( Connection c = Database.getConnection();
             PreparedStatement pstmt = c.prepareStatement(query);
        ){
            pstmt.setString(1, login);
            pstmt.setString(2, mdp);
            pstmt.setString(3, nom);
            pstmt.setString(4,prenom);
            pstmt.setString(5,email);
            pstmt.setDate(6,birthDate);
            pstmt.setString(7,country);

            pstmt.executeUpdate();
            return true;
        } catch (Exception E){
            return false;
        }
    }

    public static boolean unsubscribe(String login){
        String query = "DELETE FROM USERS WHERE login=?";

        try( Connection c = Database.getConnection();
             PreparedStatement pstmt = c.prepareStatement(query);
        ){
            pstmt.setString(1, login);
            pstmt.executeUpdate();
            return true;
        }catch (Exception e){
            return false;
        }
    }


}
