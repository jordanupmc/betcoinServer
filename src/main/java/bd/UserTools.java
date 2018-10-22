package bd;


import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;


import java.net.URISyntaxException;
import java.sql.*;
import java.util.List;
import static bd.SessionTools.removeSessionUser;
import static bd.Database.getMongoCollection;

public class UserTools {

    /* inscription d'un nouvel utilisateur */
    public static boolean subscribe(String login, String mdp, String email, String nom, String prenom, Date birthDate, String country) throws SQLException {
        String query =
                "INSERT INTO USERS(login,password, last_name, first_name, email, birthday, country)" +
                        "VALUES (?, crypt(? , gen_salt('bf', 8)), ?, ?, ?, ?, ?);";
        try (Connection c = Database.getConnection();
             PreparedStatement pstmt = c.prepareStatement(query);
        ) {
            pstmt.setString(1, login);
            pstmt.setString(2, mdp);
            pstmt.setString(3, nom);
            pstmt.setString(4, prenom);
            pstmt.setString(5, email);
            pstmt.setDate(6, birthDate);
            pstmt.setString(7, country);

            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            if(e.getMessage().contains("users_login_key"))
                throw new SQLException("login "+login + " already exists");
            return false;
        }
    }

    /*desinscription: suppression de la table users + suppression dans Session*/
    public static boolean unsubscribe(String login) {
        String query = "UPDATE USERS SET isLock=true WHERE login=?";

        try (Connection c = Database.getConnection();
             PreparedStatement pstmt = c.prepareStatement(query);
        ) {
            pstmt.setString(1, login);
            pstmt.executeUpdate();
            return removeSessionUser(login);
        } catch (Exception e) {
            return false;
        }
    }

    /* renvois un JSON avec toutes les informations affichable de l'utilisateur */
    public static JSONObject visualiseAccount(String login) throws URISyntaxException, SQLException {
        String query = "SELECT * FROM USERS WHERE login=?";
        JSONObject json = new JSONObject();

        try(Connection c = Database.getConnection();
        PreparedStatement pstmt = c.prepareStatement(query);) {
            pstmt.setString(1, login);
            pstmt.execute();
            ResultSet data = pstmt.getResultSet();
            data.next();
            json.put("login", data.getString("login"));
            json.put("email", data.getString("email"));
            json.put("last_name", data.getString("last_name"));
            json.put("first_name", data.getString("first_name"));
            json.put("birthday", data.getString("birthday"));
            json.put("country", data.getString("country"));

            MongoCollection<Document> collection = getMongoCollection("SubscribePool");
            Document d =
                    collection
                            .find(new BsonDocument().append("gamblerLogin", new BsonString(login)))
                            .first();
            if (d != null) {
                List<Document> pools = (List<Document>) d.get("idBetPool");
                JSONArray arr = new JSONArray();
                for (int i = 0; i < pools.size(); i++) {
                    Document tmp = pools.get(i);
                    arr.put(tmp);
                }
                json.put("subscribePools", arr);
                collection = getMongoCollection("Bet");
                JSONArray arr_bet = new JSONArray();
                FindIterable<Document> listdoc = collection
                        .find(new BsonDocument().append("gamblerLogin", new BsonString(login)));
                listdoc.forEach(new Block<Document>() {
                    @Override
                    public void apply(Document document) {
                        Document gros_doc = new Document();
                        gros_doc.append("idBetPool", document.get("idBetPool"));
                        gros_doc.append("betAmount", document.get("betAmount"));
                        gros_doc.append("betValue", document.get("betValue"));
                        gros_doc.append("betDate", document.get("betDate"));
                        arr_bet.put(gros_doc);
                    }
                });

                json.put("bets", arr_bet);
            }
            data.close();
        }

        return json;
    }

    /* Vérifie pour un login et un mdp donné qu'il s'agit d'un login valide et qu'il s'agit du bon mdp */
    public static boolean checkPasswd(String login, String mdp) throws URISyntaxException, SQLException {
        Connection co = Database.getConnection();

        String query = "SELECT * FROM USERS WHERE login=? AND password= crypt(?, password)";
        PreparedStatement pstmt = co.prepareStatement(query);
        pstmt.setString(1, login);
        pstmt.setString(2, mdp);
        ResultSet res = pstmt.executeQuery();
        if (res.next()) {
            pstmt.close();
            co.close();
            return true;
        }
        pstmt.close();
        co.close();
        return false;
    }

    /* Modifie les informations du compte utilisateur */
    public static boolean accountModification(String login, String field_name, String new_value) throws URISyntaxException, SQLException {
        String query = "UPDATE USERS SET " + field_name + "=? WHERE login=?";

        try(Connection c = Database.getConnection();
        PreparedStatement pstmt = c.prepareStatement(query);) {
            pstmt.setString(1, new_value);
            pstmt.setString(2, login);
            pstmt.executeUpdate();
        }
        return true;

    }

    /* Renvoi true si compte est ferme*/
    public static boolean accountClosed(String login) throws SQLException, URISyntaxException {
        Connection co = Database.getConnection();
        String query = "SELECT * FROM USERS WHERE login=?";
        PreparedStatement pstmt = co.prepareStatement(query);
        pstmt.setString(1, login);


        ResultSet res = pstmt.executeQuery();
        if (res.next() && res.getBoolean("islock")) {
            pstmt.close();
            co.close();
            return true;
        }
        pstmt.close();
        co.close();
        return false;
    }

}
