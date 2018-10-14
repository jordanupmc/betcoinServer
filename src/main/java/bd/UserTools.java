package bd;


import com.mongodb.client.MongoCollection;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.json.JSONArray;
import com.mongodb.client.result.DeleteResult;
import org.json.JSONObject;

import javax.swing.*;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.Calendar;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import static bd.Database.getMongoCollection;
import static bd.SessionTools.generateToken;
import static services.ServiceTools.serviceKO;
import static services.ServiceTools.serviceOK;

public class UserTools {
    static int everydayConnectionReward = 100;

    public static boolean subscribe(String login, String mdp, String email, String nom, String prenom, Date birthDate, String country) {
        String query =
                "INSERT INTO USERS(login,password, last_name, first_name, email, birthday, country)" +
                        "VALUES (?, ?, ?, ?, ?, ?, ?);";
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
        } catch (Exception E) {
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

    /* Permet à l'utilisateur de se déconnecter */
    public static boolean disconnect(String login,String token){
        MongoCollection<Document> sesCollection = getMongoCollection("Session");

        Document is_here = sesCollection.find(eq("login", login)).first();
        if(is_here!=null) {
            if (userConnected(login)) {
                sesCollection.updateOne(and(eq("login", login), eq("token", token)), new Document("$unset", new Document("token", "")));
            }else{
                JOptionPane.showMessageDialog(null,"Disconnection Fail : User already disconnected");
                return false;
            }
        }else{
            JOptionPane.showMessageDialog(null,"Disconnection Fail : User not found");
            return false;
        }

        return true;
    }

    /* renvois un JSON avec toutes les informations affichable de l'utilisateur */
    public static JSONObject visualiseAccount(String login){
        String query = "SELECT * FROM USERS WHERE login=?";
        JSONObject json = new JSONObject();
        try{
            Connection c = Database.getConnection();
            PreparedStatement pstmt = c.prepareStatement(query);
            pstmt.setString(1,login);
            pstmt.execute();
            ResultSet data = pstmt.getResultSet();
            data.next();
            json.put("login",data.getString("login"));
            json.put("email",data.getString("email"));
            json.put("last_name",data.getString("last_name"));
            json.put("first_name",data.getString("first_name"));
            json.put("birthday",data.getString("birthday"));
            json.put("country",data.getString("country"));
            MongoCollection<Document> collection = getMongoCollection("SubscribePool");
            Document d =
                    collection
                            .find(new BsonDocument().append("gamblerLogin", new BsonString(login)))
                            .first();
            if(d!=null) {
                List<Document> pools = (List<Document>) d.get("idBetPool");
                JSONArray arr = new JSONArray();
                for (int i = 0; i < pools.size(); i++) {
                    Document tmp = pools.get(i);
                    arr.put(tmp);
                }
                json.put("subscribePools", arr);
                collection = getMongoCollection("Bet");
                JSONArray arr_bet = new JSONArray();
                List<Document> listdoc = (List<Document>) collection
                        .find(new BsonDocument().append("gamblerLogin", new BsonString(login)));
                for (int j = 0; j < listdoc.size(); j++) {
                    arr_bet.put(listdoc.get(j));
                }

                json.put("bets", arr_bet);
            }
        }catch(Exception e){
            return null;
        }
        return json;
    }


    private static boolean removeSessionUser(String login){
        MongoCollection<Document> collection = getMongoCollection("Session");
        DeleteResult d = collection.deleteOne(new BsonDocument().append("login", new BsonString(login)));
        return d.getDeletedCount()  == 1;
    }

    /* Ajoute dans la base MongoDB, une nouvelle personne connectee avec un token unique */
    public static String connect(String login, String mdp) throws URISyntaxException, SQLException {
        MongoCollection<Document> collection = getMongoCollection("Session");
        String token = generateToken();

        Document firstConnection =
                collection
                        .find(new BsonDocument().append("login", new BsonString(login)))
                        .first();

        if(firstConnection == null){ //Premiere connection
            Document d = new Document("login", login)
                    .append("token", token)
                    .append("lastConnection", new Timestamp(System.currentTimeMillis()));

            collection.insertOne(d);
        }else{
            java.util.Date date = firstConnection.getDate("lastConnection");
            Calendar cal = Calendar.getInstance();
            int todayDay = cal.get(Calendar.DAY_OF_YEAR);
            int todayYear = cal.get(Calendar.YEAR);

            cal.setTime(date);
            int lastCoDay = cal.get(Calendar.DAY_OF_YEAR);
            int lastCoYear = cal.get(Calendar.YEAR);

            if(lastCoDay < todayDay || lastCoYear<todayYear){
                Connection co = Database.getConnection();

                String query = "UPDATE USERS SET solde=solde+? WHERE login=?";
                PreparedStatement pstmt = co.prepareStatement(query);
                pstmt.setInt(1, everydayConnectionReward);
                pstmt.setString(2, login);
                pstmt.executeUpdate();
                pstmt.close();
                co.close();

            }

            BsonDocument filter = new BsonDocument().append("login", new BsonString(login));
            collection.updateOne(filter, new Document("$set", new Document("token", token)));
            collection.updateOne(filter, new Document("$set", new Document("lastConnection", new Timestamp(System.currentTimeMillis()))));
        }

        return token;
    }

    /* Vérifie pour un login et un mdp donné qu'il s'agit d'un login valide et qu'il s'agit du bon mdp */
    public static boolean checkPasswd(String login, String mdp) throws URISyntaxException, SQLException {
        Connection co = Database.getConnection();

        String query = "SELECT * FROM USERS WHERE login=? AND password=?";
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

    /* Renvoi true si l'utilisateur login est connecté */
    public static boolean userConnected(String login) {
        MongoCollection<Document> collection = getMongoCollection("Session");
        Document d =
                collection
                        .find(new BsonDocument().append("login", new BsonString(login)))
                        .first();

        if (d == null || d.getString("token") == null)
            return false;

        return true;
    }

    /* Modifie les informations du compte utilisateur */
    public static boolean accountModification(String login, String field_name, String new_value) {
        String query = "UPDATE USERS SET "+field_name+"=? WHERE login=?";

        try (Connection c = Database.getConnection();
             PreparedStatement pstmt = c.prepareStatement(query);
        ) {

            pstmt.setString(1, new_value);
            pstmt.setString(2, login);

            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,e.toString());
            return false;
        }
    }
    /* Renvoi true si comtpe est ferme*/
    public static boolean accountClosed(String login) throws SQLException, URISyntaxException {
        Connection co = Database.getConnection();
        String query = "SELECT * FROM USERS WHERE login=?";
        PreparedStatement pstmt = co.prepareStatement(query);
        pstmt.setString(1, login);


        ResultSet res = pstmt.executeQuery();
        if (res.next() && res.getBoolean("islock")){
            pstmt.close();
            co.close();
            return true;
        }
        pstmt.close();
        co.close();
        return false;
    }

}
