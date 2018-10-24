package bd;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.util.JSON;
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
import java.util.ArrayList;
import java.util.List;

import static bd.Database.getMongoCollection;
import static bd.PoolTools.poolExist;
import static com.mongodb.client.model.Filters.and;

public class BetTools {

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

    public static boolean checkPoolResult(String idPool){
        MongoCollection<Document> collection = getMongoCollection("L_Bet");
        Document d_tmp =
                collection
                        .find(new BsonDocument().append("idBetPool", new BsonString(idPool)))
                        .first();
        if (d_tmp.getDouble("resultValue") == null) {
            return false;
        }
        return true;
    }

    public static boolean hasEnoughCoin(String login,String ammount) throws SQLException, URISyntaxException {
        String query = "SELECT solde FROM USERS WHERE login=?";
        try (Connection c = Database.getConnection();
             PreparedStatement pstmt = c.prepareStatement(query);) {
            pstmt.setString(1, login);
            ResultSet result = pstmt.executeQuery();
            result.next();
            int solde = result.getInt(1);
            solde = solde - Integer.parseInt(ammount);
            if (solde < 0) {
                return false;
            }

        }
        return true;
    }
    /* renvois la liste des salons de pari encore actif */
    public static JSONArray getListPoolsActive() {
        JSONArray ar = new JSONArray();
        String query =
                "SELECT idbetpool, name, openingbet, closingbet, resultbet, cryptocurrency, pooltype FROM BetPool WHERE closingBet > NOW() AT TIME ZONE  'Europe/Paris'";
        try (Connection c = Database.getConnection();
             PreparedStatement pstmt = c.prepareStatement(query);
             ResultSet v = pstmt.executeQuery();
        ) {
            JSONObject j = null;

            while (v.next()) {
                j = new JSONObject();
                j.put("idbetpool", v.getInt(1));
                j.put("name", v.getString(2));
                j.put("openingbet", v.getTimestamp(3));
                j.put("closingbet", v.getTimestamp(4));
                j.put("resultbet", v.getTimestamp(5));
                j.put("cryptocurrency", v.getString(6));
                j.put("pooltype", v.getBoolean(7));
                ar.put(j);
            }
        } catch (Exception e) {
        } finally {
            return ar;
        }
    }


    /* Permet aux utilisateurs d'ajouter un nouveau pari*/
    public static boolean addBet(String idPool, String login, int betAmmount, double betValue) throws URISyntaxException, SQLException {
        MongoCollection<Document> collection = getMongoCollection("L_Bet");
        Document d =
                collection
                        .find(new BsonDocument().append("idBetPool", new BsonString(idPool)))
                        .first();
        Timestamp tsp = new Timestamp(System.currentTimeMillis());
        if (d == null) {
            Document newpool = new Document();
            newpool.append("idBetPool", idPool);
            newpool.append("resultValue", "");
            ArrayList<Document> bets = new ArrayList<>();
            Document bet_obj = new Document();
            bet_obj.append("gamblerLogin", login);
            bet_obj.append("betAmount", betAmmount);
            bet_obj.append("betValue", betValue);
            bet_obj.append("betDate", tsp.toString());
            bets.add(bet_obj);
            newpool.append("bet", bets);
            collection.insertOne(newpool);

            d = collection
                    .find(new BsonDocument().append("idBetPool", new BsonString(idPool)))
                    .first();
        } else {
            d = collection.find(new BsonDocument().append("idBetPool", new BsonString(idPool))).first();
            List<Document> list_bet = (List<Document>) d.get("bet");
            Document bet_obj = new Document();
            bet_obj.append("gamblerLogin", login);
            bet_obj.append("betAmount", betAmmount);
            bet_obj.append("betValue", betValue);
            bet_obj.append("betDate", tsp.toString());
            list_bet.add(bet_obj);
            BsonDocument filter = new BsonDocument().append("idBetPool", new BsonString(idPool));
            collection.updateOne(filter, new Document("$set", new Document("bet", list_bet)));
        }

        String query = "SELECT solde FROM USERS WHERE login=?";
        try (Connection c = Database.getConnection();
             PreparedStatement pstmt = c.prepareStatement(query);) {
            pstmt.setString(1, login);
            ResultSet result = pstmt.executeQuery();
            result.next();

            int solde = result.getInt(1);
            solde = solde - betAmmount;
            query = "UPDATE USERS SET solde=? WHERE login=?";
            try (PreparedStatement pstmt2 = c.prepareStatement(query)) {
                pstmt2.setInt(1, solde);
                pstmt2.setString(2, login);
                pstmt2.executeUpdate();
            }
        }


        collection = getMongoCollection("Bet");
        Document obj = new Document();
        obj.append("gamblerLogin", login);
        obj.append("idBetPool", idPool);
        obj.append("betAmount", betAmmount);
        obj.append("betValue", betValue);
        obj.append("betDate", tsp.toString());
        collection.insertOne(obj);

        return true;

    }

    public static boolean checkBetExist(String login, String idPool) {
        MongoCollection<Document> collection = getMongoCollection("Bet");
        Document d =
                collection
                        .find(and(new BsonDocument().append("idBetPool", new BsonString(idPool)),
                                new BsonDocument().append("gamblerLogin", new BsonString(login))))
                        .first();
        if (d != null) return true;
        return false;
    }

    /* Renvoi true si un pari est toujours annulable */
    public static boolean betPoolOpen(String idPool) throws URISyntaxException, SQLException {

        String query = "SELECT * FROM BETPOOL WHERE idbetpool=?";

        try (Connection c = Database.getConnection();
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
                    pstmt.setInt(1, toRefound);
                    pstmt.setString(2, login);
                    pstmt.executeUpdate();
                    pstmt.close();
                    collection = getMongoCollection("Bet");
                    d = collection
                            .find(and(new BsonDocument().append("idBetPool", new BsonString(idPool)),
                                    new BsonDocument().append("gamblerLogin", new BsonString(login))))
                            .first();
                    if(d==null){
                        return false;
                    }
                    collection.deleteOne(and(new BsonDocument().append("idBetPool", new BsonString(idPool)),
                            new BsonDocument().append("gamblerLogin", new BsonString(login))));
                    return true;
                }
            }

            return false;
        }
    }

    public static int betWon(String login, String idPool) throws URISyntaxException, SQLException {
        MongoCollection<Document> collection = getMongoCollection("L_Bet");
        Document d_tmp =
                collection
                        .find(new BsonDocument().append("idBetPool", new BsonString(idPool)))
                        .first();
        double resultValue = d_tmp.getDouble("resultValue");
        collection = getMongoCollection("Bet");
        Document d =
                collection
                        .find(and(new BsonDocument().append("idBetPool", new BsonString(idPool)),
                                new BsonDocument().append("gamblerLogin", new BsonString(login))))
                        .first();
        double betValue = d.getDouble("betValue");
        if (betValue == resultValue) {
            String amount_s = d.get("betAmount").toString();
            int amount_i = Integer.parseInt(amount_s);
            String query = "SELECT solde FROM USERS WHERE login=?";

            try (Connection c = Database.getConnection();
                 PreparedStatement pstmt = c.prepareStatement(query);) {
                pstmt.setString(1, login);
                ResultSet res = pstmt.executeQuery();
                res.next();
                int soldeAccount = res.getInt(1);
                res.close();
                int amountWon;
                boolean type;
                query = "SELECT pooltype FROM BETPOOL WHERE idbetpool=?";
                try (PreparedStatement pstmt3 = c.prepareStatement(query);) {
                    pstmt3.setString(1, idPool);
                    ResultSet restype = pstmt3.executeQuery();
                    type = restype.getBoolean(1);
                    restype.close();
                }
                if (type) {
                    amountWon = amount_i * 5;
                } else {
                    amountWon = (int) Math.round(amount_i * 1.2);
                }
                soldeAccount = soldeAccount + amountWon;
                query = "UPDATE USERS SET solde=? WHERE login=?";
                try (PreparedStatement pstmt2 = c.prepareStatement(query);) {
                    pstmt2.setInt(1, soldeAccount);
                    pstmt2.setString(2, login);
                    pstmt2.executeQuery();
                }
                return amountWon;
            }
        }
        return -1;
    }

    public static JSONArray getListBets(String login) {
        MongoCollection<Document> collection = getMongoCollection("Bet");
        JSONArray array = new JSONArray();
        try(MongoCursor<Document> d_tmp =
                collection
                        .find(new BsonDocument().append("gamblerLogin", new BsonString(login))).iterator();) {

            while (d_tmp.hasNext()) {
                Document tmp = d_tmp.next();
                array.put(tmp);
            }
        }
        return array;

    }

    public static Document getBet(String login, String idPool) {
        MongoCollection<Document> collection = getMongoCollection("Bet");
        Document d_tmp =
                collection
                        .find(and(new BsonDocument().append("gamblerLogin", new BsonString(login)),
                                new BsonDocument().append("idBetPool", new BsonString(idPool))))
                        .first();
        return d_tmp;
    }
}
