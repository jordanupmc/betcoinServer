package bd;

import com.mongodb.client.MongoCollection;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static bd.Database.getMongoCollection;

public class BetTools {

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
                j.put("resultbet", v.getBigDecimal(5));
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


    /* Permet aux utilisateurs de quitter/se désinscrire d'un salon de pari */
    public static boolean quitPool(String login, String idPool) throws URISyntaxException, SQLException {

        MongoCollection<Document> collection = getMongoCollection("SubscribePool");
        Document d =
                collection
                        .find(new BsonDocument().append("gamblerLogin", new BsonString(login)))
                        .first();
        if(d==null){
            JOptionPane.showMessageDialog(null,"User has no subscription yet");
            return false;
        }else{
            BsonDocument filter = new BsonDocument().append("gamblerLogin", new BsonString(login));
            List<Document> pools = (List<Document>) d.get("idBetPool");
            for(int i = 0; i < pools.size();i++){
                if(pools.get(i).get("idPool").equals(idPool)){
                    pools.remove(i);
                    collection.updateOne(filter, new Document("$set", new Document("idBetPool", pools)));
                    cancelBet(login, idPool);
                    return true;
                }
            }
            return false;
        }
    }

    /* Permet aux utilisateurs d'ajouter un nouveau pari*/
    public static boolean addBet(String idPool, String login, int betAmmount, double betValue){
        MongoCollection<Document> collection = getMongoCollection("Bet");
        Document d =
                collection
                        .find(new BsonDocument().append("idBetPool", new BsonString(idPool)))
                        .first();
        if (d == null) {
            return false;
        }
        try {
            if (!betPoolOpen(idPool)) {
                return false;
            }
        }catch(Exception e){
            return false;
        }
        Document obj = new Document();
        obj.append("gamblerLogin",login);
        obj.append("betAmount",betAmmount);
        obj.append("betValue",betValue);
        Timestamp tsp = new Timestamp(System.currentTimeMillis());
        obj.append("betDate",tsp.toString());
        collection.insertOne(obj);

        collection = getMongoCollection("L_Bet");
        d = collection.find(new BsonDocument().append("idPool", new BsonString(idPool))).first();
        d.append("bet",obj);
        BsonDocument filter = new BsonDocument().append("idPool", new BsonString(idPool));
        collection.updateOne(filter, new Document("$set", d));

        return true;

    }

    /* Renvoi true si un pari est toujours annulable */
    public static boolean betPoolOpen(String idPool) throws URISyntaxException, SQLException {

        Connection c = Database.getConnection();
        String query = "SELECT * FROM BETPOOL WHERE idbetpool=?";

        PreparedStatement pstmt = c.prepareStatement(query);

        pstmt.setInt(1, Integer.parseInt(idPool));

        ResultSet res = pstmt.executeQuery();
        if (res.next()) {
            if(res.getTimestamp("closingbet").after(new Timestamp(System.currentTimeMillis()))){
                pstmt.close();
                c.close();
                return true;
            }
        }
        pstmt.close();
        c.close();
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
