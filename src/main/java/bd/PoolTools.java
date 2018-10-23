package bd;

import com.mongodb.client.MongoCollection;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;


import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static bd.BetTools.cancelBet;
import static bd.Database.getMongoCollection;
import static services.APIService.getCryptoCurrency;


public class PoolTools {

    /*check si la pool existe*/
    public static boolean poolExist(String idPool) throws URISyntaxException, SQLException {

        String query = "SELECT * FROM BETPOOL WHERE idbetpool=?";
        try (Connection co = Database.getConnection();
             PreparedStatement pstmt = co.prepareStatement(query);) {
            pstmt.setInt(1, Integer.parseInt(idPool));
            ResultSet res = pstmt.executeQuery();
            if (res.next()) {
                pstmt.close();
                co.close();
                return true;
            }
        }
        return false;
    }

    /* Fais entrer un utilisateur login dans la pool idPool, s'il était deja dedans ne fais rien */
    public static void enterPool(String login, String idPool) {
        MongoCollection<Document> collection = getMongoCollection("SubscribePool");
        Document d =
                collection
                        .find(new BsonDocument().append("gamblerLogin", new BsonString(login)))
                        .first();

        if (d == null) {
            List<Document> idBetPools = new ArrayList<>();
            idBetPools.add(new Document("idPool", new BsonString(idPool)));
            Document toInsert = new Document("gamblerLogin", login)
                    .append("idBetPool", idBetPools);
            collection.insertOne(toInsert);
            return;
        } else {
            BsonDocument filter = new BsonDocument().append("gamblerLogin", new BsonString(login));
            List<Document> idBetPools = (List<Document>) d.get("idBetPool");

            for (int i = 0; i < idBetPools.size(); i++) {
                if (idBetPools.get(i).get("idPool").equals(idPool)) {
                    return;
                }
            }

            idBetPools.add(new Document("idPool", new BsonString(idPool)));
            collection.updateOne(filter, new Document("$set", new Document("idBetPool", idBetPools)));

        }
    }

    /* Ajoute un message de login dans la pool ayant idPool*/
    public static void messagePool(String login, String idPool, String message) {
        MongoCollection<Document> collection = getMongoCollection("L_Message");

        Document d =
                collection
                        .find(new BsonDocument().append("idBetPool", new BsonString(idPool)))
                        .first();

        Document msgToInsert = new Document("gamblerLogin", login)
                .append("text", message)
                .append("messageDate", new Timestamp(System.currentTimeMillis()));

        if (d == null) {
            List<Document> messages = new ArrayList<>();
            messages.add(msgToInsert);

            Document allToInsert = new Document("ibBetPool", idPool)
                    .append("messages", messages);
            collection.insertOne(allToInsert);

        } else {

            List<Document> messages = (List<Document>) d.get("messages");
            BsonDocument filter = new BsonDocument().append("idBetPool", new BsonString(idPool));

            messages.add(msgToInsert);
            collection.updateOne(filter, new Document("$set", new Document("messages", messages)));
        }
    }

    /* Permet aux utilisateurs de quitter/se désinscrire d'un salon de pari */
    public static boolean quitPool(String login, String idPool) throws URISyntaxException, SQLException {

        MongoCollection<Document> collection = getMongoCollection("SubscribePool");
        Document d =
                collection
                        .find(new BsonDocument().append("gamblerLogin", new BsonString(login)))
                        .first();

        BsonDocument filter = new BsonDocument().append("gamblerLogin", new BsonString(login));
        List<Document> pools = (List<Document>) d.get("idBetPool");
        for (int i = 0; i < pools.size(); i++) {
            if (pools.get(i).get("idPool").equals(idPool)) {
                pools.remove(i);
                collection.updateOne(filter, new Document("$set", new Document("idBetPool", pools)));
                cancelBet(login, idPool);
                return true;
            }
        }
        return false;
    }

    public static boolean createPool(CryptoEnum cryptoEnum, boolean poolType) {
        String query =
                "INSERT INTO BetPool (openingBet, cryptoCurrency, poolType, openingprice) VALUES (NOW() AT TIME ZONE  'Europe/Paris',  CAST ( ? AS crypto_currency), ? , ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement pstmt = c.prepareStatement(query)
        ) {
            //pstmt.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
            pstmt.setString(1, cryptoEnum.readable());
            pstmt.setBoolean(2, poolType);
            long timestp = System.currentTimeMillis();
            JSONObject json = getCryptoCurrency(cryptoEnum.toString(),"EUR",
                    ""+timestp,""+timestp,1);
            JSONArray result = (JSONArray) json.get("results");
            Document data = (Document) result.get(0);
            ArrayList<Document> data_arr = (ArrayList<Document>) data.get("Data");
            Document objFinal = data_arr.get(0);
            double value = (double) objFinal.get("close");
            pstmt.setDouble(3, value);

            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            //TODO remove le sout
            System.out.println(e);
            return false;
        }
    }

}
