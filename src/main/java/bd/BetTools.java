package bd;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.util.JSON;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
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

    public static boolean checkPoolResult(String idPool){
        MongoCollection<Document> collection = getMongoCollection("L_Bet");
        Document d_tmp =
                collection
                        .find(new BsonDocument().append("idBetPool", new BsonString(idPool)))
                        .first();
        if (d_tmp.getDouble("resultValue") == -1) {
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
            newpool.append("resultValue", 0.0);
            ArrayList<Document> bets = new ArrayList<>();
            Document bet_obj = new Document();
            bet_obj.append("gamblerLogin", login);
            bet_obj.append("betAmount", betAmmount);
            bet_obj.append("betValue", betValue);
            bet_obj.append("betDate", tsp.toString());
            bet_obj.append("haveCheckResult", false);
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
            bet_obj.append("haveCheckResult", false);
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
        obj.append("haveCheckResult", false);
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

    public static int betWon(String login, String idPool) throws URISyntaxException, SQLException, IOException {
        MongoCollection<Document> collection = getMongoCollection("L_Bet");
        Document d_tmp =
                collection
                        .find(new BsonDocument().append("idBetPool", new BsonString(idPool)))
                        .first();
        double resultValue = d_tmp.getDouble("resultValue");

        // On est la premiere personne a vérifier son pari, dans ce cas on va chercher la valeur du resultat dans l'API et on rempli notre BD avec ce resultat
        if(resultValue == 0){
            resultValue = updateDBResultValue(idPool, login);
            if(resultValue == 0){
                return -1;
            }
        }

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

            return updateAccountGoldWin(amount_i, idPool, login);

        }
        return 0;
    }

    private static int updateAccountGoldWin(int ammountBet, String idPool, String login) throws URISyntaxException, SQLException {

        String query = "SELECT solde FROM USERS WHERE login=?";

        try (Connection c = Database.getConnection();
             PreparedStatement pstmt = c.prepareStatement(query);) {
            pstmt.setString(1, login);
            ResultSet res = pstmt.executeQuery();
            if(res.next()) {
                int soldeAccount = res.getInt(1);
                res.close();
                int amountWon = 0;
                boolean type;
                query = "SELECT pooltype FROM BETPOOL WHERE idbetpool=?";
                try (PreparedStatement pstmt3 = c.prepareStatement(query);) {
                    pstmt3.setInt(1, Integer.parseInt(idPool));
                    ResultSet restype = pstmt3.executeQuery();
                    if(restype.next()) {
                        type = restype.getBoolean(1);
                        restype.close();
                        if (type) {
                            amountWon = ammountBet * 5;
                        } else {
                            amountWon = (int) Math.round(ammountBet * 1.2);
                        }
                    }
                }

                soldeAccount = soldeAccount + amountWon;
                query = "UPDATE USERS SET solde=? WHERE login=?";
                try (PreparedStatement pstmt2 = c.prepareStatement(query);) {
                    pstmt2.setInt(1, soldeAccount);
                    pstmt2.setString(2, login);
                    pstmt2.executeUpdate();
                }
                return amountWon;
            }
        }
        return -1;

    }

    private static double updateDBResultValue(String idPool, String login) throws URISyntaxException, SQLException, IOException {

        String query = "SELECT * FROM betpool WHERE idbetpool=?";

        try (Connection c = Database.getConnection();
             PreparedStatement pstmt = c.prepareStatement(query);) {
            pstmt.setInt(1, Integer.parseInt(idPool));
            ResultSet res = pstmt.executeQuery();
            if(res.next()) {
                String crypt = res.getString("cryptocurrency");
                Timestamp time = res.getTimestamp("resultbet");
                Boolean poolType = res.getBoolean("pooltype");
                double openingPrice = res.getDouble("openingprice");

                res.close();


                double resultValue = APITools.getPriceSpecificTime(getCryptNickname(crypt),"EUR",time.getTime()+"");


                MongoCollection<Document> collection = getMongoCollection("L_Bet");

                BsonDocument filter = new BsonDocument().append("idBetPool", new BsonString(idPool));
                if(poolType) {
                    collection.updateOne(filter, new Document("$set", new Document("resultValue", resultValue)));
                }else{
                    collection.updateOne(filter, new Document("$set", new Document("resultValue", (resultValue > openingPrice)? 1.0 : -1.0)));
                }
                return resultValue;
            }
        }
        return 0;
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

    public static boolean betResultIsAvailable(String idPool) throws URISyntaxException, SQLException {
        String query = "SELECT * FROM BETPOOL WHERE idbetpool=?";

        try (Connection c = Database.getConnection();
             PreparedStatement pstmt = c.prepareStatement(query);) {
            pstmt.setInt(1, Integer.parseInt(idPool));
            ResultSet res = pstmt.executeQuery();

            if (res.next()) {
                if (res.getTimestamp("resultbet").before(new Timestamp(System.currentTimeMillis()))) {
                    pstmt.close();
                    c.close();
                    return true;
                }
            }
        }
        return false;

    }



    private static String getCryptNickname(String cryptFullName){
        switch (cryptFullName){
            case "Bitcoin": return "BTC";
            case "Ethereum": return "ETH";
            case "EthereumClassic": return "ETC";
            case "LiteCoin": return "LTC";
            case "BitcoinCash" : return "BCH";
            case "ZCash": return "ZCH";
            case "Dash": return "DASH";
            default: return cryptFullName;
        }
    }
}
