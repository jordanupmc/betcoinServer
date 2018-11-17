package bd;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.util.JSON;
import org.bson.*;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;


import javax.print.Doc;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.*;
import java.util.Date;

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
                .append("_msgId", new ObjectId())
                .append("messageDate", new Timestamp(System.currentTimeMillis()));

        if (d == null) {
            List<Document> messages = new ArrayList<>();
            messages.add(msgToInsert);

            Document allToInsert = new Document("idBetPool", idPool)
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
                return true;
            }
        }
        return false;
    }

    public static boolean createPool(CryptoEnum cryptoEnum, boolean poolType) {
        String query ="";
        if(!poolType)
            query="INSERT INTO BetPool (cryptoCurrency, poolType, openingprice) VALUES (CAST ( ? AS crypto_currency), ? , ?)";
        else
            query="INSERT INTO BetPool (cryptoCurrency, poolType) VALUES (CAST ( ? AS crypto_currency), ?)";

        try (Connection c = Database.getConnection();
             PreparedStatement pstmt = c.prepareStatement(query)
        ) {
            //pstmt.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
            pstmt.setString(1, cryptoEnum.readable());
            pstmt.setBoolean(2, poolType);
            if(!poolType) {
                long timestp = System.currentTimeMillis();
                JSONObject json = getCryptoCurrency(cryptoEnum.toString(), "EUR",
                        "" + timestp, "" + timestp, 1);
                JSONArray result = (JSONArray) json.get("results");
                Document data = (Document) result.get(0);
                ArrayList<Document> data_arr = (ArrayList<Document>) data.get("Data");
                Document objFinal = data_arr.get(0);
                double value;
                if(objFinal.get("close") instanceof Double){
                    value = objFinal.getDouble("close");
                }else{
                    value = objFinal.getInteger("close") + 0.0;
                }
                pstmt.setDouble(3, value);
            }
            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            //TODO remove le sout
            System.out.println(e);
            return false;
        }
    }

    /*Cree une pool par crypto monnaie*/
    public static boolean createAllPool(boolean poolType){
        boolean tmp = true;

        for(int i =0; i< CryptoEnum.values().length; i++){
            CryptoEnum curr = CryptoEnum.values()[i];
            tmp = createPool(curr, poolType);
            if(!tmp)return tmp;
        }
        return tmp;
    }

    public static JSONObject poolInfo(String idPool) throws URISyntaxException, SQLException {
        JSONObject json = new JSONObject();
        String query = "SELECT * FROM BETPOOL WHERE idbetpool=?";
        try (Connection c = Database.getConnection();
             PreparedStatement pstmt = c.prepareStatement(query)
        ) {
            pstmt.setInt(1, Integer.parseInt(idPool));
            ResultSet result = pstmt.executeQuery();
            result.next();
            json.put("idbetpool", result.getInt(1));
            json.put("name", result.getString(2));
            json.put("openingbet", result.getTimestamp(3));
            json.put("closingbet", result.getTimestamp(4));
            json.put("resultbet", result.getTimestamp(5));
            json.put("cryptocurrency", result.getString(6));
            json.put("pooltype", result.getBoolean(7));
        }
        return json;
    }

    public static boolean isSubscribed(String login, String idPool) {
        MongoCollection<Document> collection = getMongoCollection("SubscribePool");
        Document d =
                collection
                        .find(new BsonDocument().append("gamblerLogin", new BsonString(login)))
                        .first();
        ArrayList<Document> array = (ArrayList<Document>) d.get("idBetPool");
        for(Document tmp : array){
            if(Integer.parseInt((String)tmp.get("idPool"))==Integer.parseInt(idPool)){
                return true;
            }
        }
        return false;
    }

    /*Return la liste des messages d'une pool qui ont été post après fromId*/
    public static JSONArray getListMessagePool(int idPool, String fromId){
        MongoCollection<Document> collection = getMongoCollection("L_Message");

        /*En Mongo console
        db.L_Message.aggregate([ { $match : { idBetPool : "1"} },
           {       $project: {
                messages: {$filter: {input: "$messages", as: "message",
                 cond: { $gt: [ "$$message._msgId", ObjectId("5bd1eb4f22515c000496ab98") ] }             }*/

        /*On recupere le tableau de messages de la pool qui a l'id idPool
        * Puis dans ce tableau on recupere les messages qui verifie _msgId > fromId ce qui permet d'avoir les messages posté apres fromId
        * */
        BsonArray array = new BsonArray();
        array.add(new BsonString("$$message._msgId"));
        array.add(new BsonObjectId(new ObjectId(fromId)));

        BsonDocument match  = new BsonDocument().append("$match", new BsonDocument().append("idBetPool",new BsonString(idPool+"")));
        BsonDocument project = new BsonDocument()
                .append("$project", new BsonDocument()
                        .append("messages", new BsonDocument()
                                .append("$filter", new BsonDocument()
                                        .append("input", new BsonString("$messages"))
                                        .append("as", new BsonString("message"))
                                        .append("cond", new BsonDocument()
                                                .append("$gt", array)))));

        Document d =
                collection
                        . aggregate(Arrays.asList(match, project)).first();

        if(d == null)
            return null;

        JSONArray tmp = new JSONObject(d.toJson()).getJSONArray("messages");

        if(tmp ==null)
            return new JSONArray();
        return tmp;
    }

    public static JSONArray getEnsembleUrlChat(JSONArray messages) throws URISyntaxException, SQLException {
        Set<String> logins = new HashSet<>();
        System.out.println("MESS LENGTH = "+messages.length());
        for(int i=0; i< messages.length(); i++){
            JSONObject current = messages.getJSONObject(i);
            logins.add(current.getString("gamblerLogin"));
        }

        return UserTools.getMultipleEmail(logins);
    }

    /*Return la liste de tout les messages d'une pool*/
    public static JSONArray getListMessagePool(int idPool){
        MongoCollection<Document> collection = getMongoCollection("L_Message");
        Document d =
                collection
                        .find(new BsonDocument().append("idBetPool", new BsonString(idPool+"")))
                        .first();
        if(d == null)
            return null;

        JSONArray tmp = new JSONObject(d.toJson()).getJSONArray("messages");

        if(tmp ==null)
            return new JSONArray();
        return tmp;
    }

    /*Supprime un message d'une pool donne*/
    public static boolean deleteMessage(String idPool, String msgId) {

        MongoCollection<Document> collection = getMongoCollection("L_Message");
        /*
        Mongo console
        db.L_Message.update( { idBetPool : "1" }, {$pull : { messages : { _msgId : ObjectId("5bd227df9eb986000492b290")  } } },  { multi: true } )
*/

        UpdateResult d = collection.updateOne(new BsonDocument().append("idBetPool", new BsonString(idPool))
                , new BsonDocument()
                        .append("$pull", new BsonDocument()
                                .append("messages", new BsonDocument()
                                        .append("_msgId", new BsonObjectId(new ObjectId(msgId))))));
        return d.isModifiedCountAvailable();
    }
}
