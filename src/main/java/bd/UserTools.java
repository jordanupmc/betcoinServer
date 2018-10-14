package bd;

import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.util.JSON;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.json.JSONArray;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.json.JSONObject;

import javax.swing.*;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import java.util.ArrayList;
import java.util.List;

import static bd.Database.getMongoCollection;
import static bd.SessionTools.generateToken;

public class UserTools {
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
        MongoClientURI uri = new MongoClientURI(Database.mongoURI);
        MongoClient client = new MongoClient(uri);
        MongoDatabase db = client.getDatabase(uri.getDatabase());
        MongoCollection<Document> sesCollection = db.getCollection("session");

        Document is_here = sesCollection.find(eq("login", login)).first();
        if(is_here!=null) {
            if (is_here.get(token) != null) {
                sesCollection.updateOne(and(eq("login", login), eq("token", token)), new Document("$unset", new Document("token", "")));
            }else{
                JOptionPane.showMessageDialog(null,"User already disconnected");
                return false;
            }
        }else{
            JOptionPane.showMessageDialog(null,"User not found");
            return false;
        }

        return true;
    }

    /* renvois un JSON avec toutes les informations affichable de l'utilisateur */
    public static JSONObject visualiseAccount(String login){
        String query = "SELECT * IN USERS WHERE login=?";
        JSONObject json = new JSONObject();
        try( Connection c = Database.getConnection();
             PreparedStatement pstmt = c.prepareStatement(query)
        ){
            pstmt.setString(1,login);
            pstmt.execute();
            ResultSet data = (ResultSet)pstmt.getMetaData();
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
            List<Document> pools = (List<Document>) d.get("idBetPool");
            JSONArray arr = new JSONArray();
            for(int i = 0; i < pools.size();i++){
                Document tmp = pools.get(i);
                arr.put(tmp);
            }
            json.put("subscribePools", arr);
            collection = getMongoCollection("Bet");
            JSONArray arr_bet = new JSONArray();
            List<Document> listdoc = (List<Document>)collection
                .find(new BsonDocument().append("gamblerLogin", new BsonString(login)));
            for(int j = 0; j < listdoc.size();j++){
                arr_bet.put(listdoc.get(j));
            }

            json.put("bets", arr_bet);
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
    public static String connect(String login, String mdp) {
        MongoCollection<Document> collection = getMongoCollection("Session");
        String token = generateToken();

        Document d = new Document("login", login)
                .append("token", token)
                .append("lastConnection", new Timestamp(System.currentTimeMillis()));

        collection.insertOne(d);

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

}
