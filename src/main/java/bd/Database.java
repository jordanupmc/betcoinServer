package bd;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;


import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static MongoDatabase mongoDB = null;

    private Database(){
    }

    /* renvois une connection à la base de données postgreSQL */
    public static Connection getConnection() throws URISyntaxException, SQLException {
        URI dbUri = new URI(System.getenv("DATABASE_URL"));
        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];

        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + dbUri.getPath()+"?sslmode=require";
        return DriverManager.getConnection(dbUrl, username, password);
    }

    /* Renvoi la collection mongoDB correspondant au parametre */
    public static MongoCollection<Document> getMongoCollection(String nomColl){
        if(mongoDB == null) {

            MongoClientURI uri = new MongoClientURI(System.getenv("MONGODB_URI"));
            MongoClient client = new MongoClient(uri);
            mongoDB = client.getDatabase(uri.getDatabase());
        }
        MongoCollection<Document> collection = mongoDB.getCollection(nomColl);
        return collection;
    }

}
