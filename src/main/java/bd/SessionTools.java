package bd;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;

import static bd.Database.getMongoCollection;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class SessionTools {
    private static int everydayConnectionReward = 100;

    /*check si un user est connecte avec un certain token*/
    public static boolean checkToken(String token, String login) {
        MongoCollection<Document> collection = getMongoCollection("Session");
        Document d =
                collection
                        .find(new BsonDocument().append("login", new BsonString(login))
                                .append("token", new BsonString(token)))
                        .first();

        if (d == null)
            return false;
        return true;
    }

    /*Genere un token qui n'est pas utilise*/
    public static String generateToken() {
        String token = UUID.randomUUID().hashCode() + "";
        while (!unusedToken(token)) {
            token = UUID.randomUUID().hashCode() + "";
        }
        return token;
    }

    /* Verifie si un token est deja utilise*/
    public static boolean unusedToken(String token) {

        MongoCollection<Document> collection = getMongoCollection("Session");
        Document d =
                collection
                        .find(new BsonDocument().append("token", new BsonString(token)))
                        .first();

        if (d == null)
            return true;
        return false;

    }

    /* suppression d'un session de connexion */
    public static boolean removeSessionUser(String login) {
        MongoCollection<Document> collection = getMongoCollection("Session");
        DeleteResult d = collection.deleteOne(new BsonDocument().append("login", new BsonString(login)));
        return d.getDeletedCount() == 1;
    }

    /* Ajoute dans la base MongoDB, une nouvelle personne connectee avec un token unique */
    public static String connect(String login, String mdp) throws URISyntaxException, SQLException {
        MongoCollection<Document> collection = getMongoCollection("Session");
        String token = generateToken();

        Document firstConnection =
                collection
                        .find(new BsonDocument().append("login", new BsonString(login)))
                        .first();

        if (firstConnection == null) { //Premiere connection
            Document d = new Document("login", login)
                    .append("token", token)
                    .append("lastConnection", new Timestamp(System.currentTimeMillis()));

            collection.insertOne(d);
        } else {
            java.util.Date date = firstConnection.getDate("lastConnection");
            Calendar cal = Calendar.getInstance();
            int todayDay = cal.get(Calendar.DAY_OF_YEAR);
            int todayYear = cal.get(Calendar.YEAR);

            cal.setTime(date);
            int lastCoDay = cal.get(Calendar.DAY_OF_YEAR);
            int lastCoYear = cal.get(Calendar.YEAR);

            if (lastCoDay < todayDay || lastCoYear < todayYear) {
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

    /* Permet à l'utilisateur de se déconnecter */
    public static boolean disconnect(String login, String token) {
        MongoCollection<Document> sesCollection = getMongoCollection("Session");

        Document is_here = sesCollection.find(eq("login", login)).first();
        if (is_here != null) {
            sesCollection.updateOne(and(eq("login", login), eq("token", token)), new Document("$unset", new Document("token", "")));
        } else {
            return false;
        }

        return true;
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
