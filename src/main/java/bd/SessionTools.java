package bd;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;

import java.util.UUID;

import static bd.Database.getMongoCollection;

public class SessionTools {


    /*check si un user est connecte avec un certain token*/
    public static boolean checkToken(String token, String login) {
        MongoCollection<Document> collection = getMongoCollection("Session");
        Document d =
                collection
                        .find(new BsonDocument().append("token", new BsonString(token))
                                .append("login", new BsonString(login)))
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
}
