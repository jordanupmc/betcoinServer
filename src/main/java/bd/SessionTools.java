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

public class SessionTools {


    /*check si le token est valide*/
    public static boolean checkToken(String token, String login){
        MongoClientURI uri  = new MongoClientURI(Database.mongoURI);
        try(MongoClient client = new MongoClient(uri);) {
            MongoDatabase db = client.getDatabase(uri.getDatabase());
            MongoCollection<Document> collection = db.getCollection("Session");
            Document d =
                    collection
                            .find(new BsonDocument().append("token", new BsonString(token))
                                                    .append("login", new BsonString(login)))
                            .first();

            if(d==null)
                return false;
            return d.getBoolean("isConnected");

        }


    }
}
