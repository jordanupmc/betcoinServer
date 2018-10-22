package bd;

import com.mongodb.client.MongoCollection;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.List;

import static bd.Database.getMongoCollection;
import static com.mongodb.client.model.Filters.and;

public class BetTools {

    /* renvois la liste des salons de pari encore actif */
    public static JSONArray getListPoolsActive(){
        JSONArray ar=new JSONArray();
        String query =
                "SELECT idbetpool, name, openingbet, closingbet, resultbet, cryptocurrency, pooltype FROM BetPool WHERE closingBet > NOW()";
        try(Connection c = Database.getConnection();
            PreparedStatement pstmt = c.prepareStatement(query);
            ResultSet v = pstmt.executeQuery();
        ) {
            JSONObject j=null;

            while(v.next()){
                j =new JSONObject();
                j.put("idbetpool", v.getInt(1));
                j.put("name", v.getString(2));
                j.put("openingbet", v.getTimestamp(3));
                j.put("closingbet", v.getTimestamp(4));
                j.put("resultbet", v.getTimestamp(5));
                j.put("cryptocurrency", v.getString(6));
                j.put("pooltype", v.getBoolean(7));
                ar.put(j);
            }
        }catch(Exception e){
        }
        finally {
            return ar;
        }
    }


    /* Permet aux utilisateurs d'ajouter un nouveau pari*/
    public static boolean addBet(String idPool, String login, int betAmmount, double betValue){
        MongoCollection<Document> collection = getMongoCollection("L_Bet");
        Document d =
                collection
                        .find(new BsonDocument().append("idBetPool", new BsonString(idPool)))
                        .first();
        if (d == null) {
            return false;
        }

        Document bet_obj = new Document();
        bet_obj.append("gamblerLogin",login);
        bet_obj.append("betAmount",betAmmount);
        bet_obj.append("betValue",betValue);
        Timestamp tsp = new Timestamp(System.currentTimeMillis());
        bet_obj.append("betDate",tsp.toString());

        d = collection.find(new BsonDocument().append("idBetPool", new BsonString(idPool))).first();
        List<Document> list_bet = (List<Document>) d.get("bet");
        list_bet.add(bet_obj);
        BsonDocument filter = new BsonDocument().append("idBetPool", new BsonString(idPool));
        collection.updateOne(filter, new Document("$set", new Document("bet", list_bet)));

        collection = getMongoCollection("Bet");
        Document obj = new Document();
        obj.append("gamblerLogin",login);
        obj.append("idBetPool",idPool);
        obj.append("betAmount",betAmmount);
        obj.append("betValue",betValue);
        obj.append("betDate",tsp.toString());
        collection.insertOne(obj);

        return true;

    }

    public static boolean checkBetExist(String login, String idPool){
        MongoCollection<Document> collection = getMongoCollection("Bet");
        Document d =
                collection
                        .find(and(new BsonDocument().append("idBetPool", new BsonString(idPool)),
                                new BsonDocument().append("gamblerLogin", new BsonString(login))))
                        .first();
        if(d!=null) return true;
        return false;
    }

    /* Renvoi true si un pari est toujours annulable */
    public static boolean betPoolOpen(String idPool) throws URISyntaxException, SQLException {

        String query = "SELECT * FROM BETPOOL WHERE idbetpool=?";

        try(Connection c = Database.getConnection();
        PreparedStatement pstmt = c.prepareStatement(query);) {

            pstmt.setInt(1, Integer.parseInt(idPool));

            ResultSet res = pstmt.executeQuery();
            if (res.next()) {
                if (res.getTimestamp("closingbet").after(new Timestamp(System.currentTimeMillis()))) {
                    pstmt.close();
                    c.close();
                    return true;
                }
            }
        }
        return false;
    }

    /* Annule un bet*/
    public static boolean cancelBet(String login, String idPool) throws URISyntaxException, SQLException {
        MongoCollection<Document> collection = getMongoCollection("L_Bet");
        Document d =
                collection
                        .find(new BsonDocument().append("idBetPool", new BsonString(idPool)))
                        .first();
        if (d == null) {
            return false;
        } else {
            BsonDocument filter = new BsonDocument().append("idBetPool", new BsonString(idPool));

            List<Document> bets = (List<Document>) d.get("bet");

            for (int i = 0; i < bets.size(); i++) {
                if (bets.get(i).get("gamblerLogin").equals(login)) {
                    int toRefound = bets.get(i).getInteger("betAmount");
                    bets.remove(i);
                    collection.updateOne(filter, new Document("$set", new Document("bet", bets)));


                    Connection co = Database.getConnection();
                    String query = "UPDATE USERS SET solde=solde+? WHERE login=?";
                    PreparedStatement pstmt = co.prepareStatement(query);
                    pstmt.setDouble(1, toRefound);
                    pstmt.setString(2, login);
                    pstmt.executeUpdate();
                    pstmt.close();
                    return true;
                }
            }

            return false;
        }
    }
}
