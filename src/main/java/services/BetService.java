package services;

import bd.BetTools;
import bd.Database;
import bd.SessionTools;
import com.mongodb.client.MongoCollection;
import com.mongodb.util.JSON;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static bd.BetTools.betPoolOpen;
import static bd.BetTools.checkBetExist;
import static bd.Database.getMongoCollection;
import static bd.PoolTools.poolExist;
import static bd.SessionTools.userConnected;
import static com.mongodb.client.model.Filters.and;
import static services.ServiceTools.serviceKO;
import static services.ServiceTools.serviceOK;

public class BetService {

    /* service pour l'ajout d'un pari par un utilisateur */
    public static JSONObject addBet(String token, String login, String idPool, String ammount, String value) throws URISyntaxException, SQLException {
        if ((login == null) || (idPool == null) || (ammount == null) || (value == null) || (token == null)) {
            return serviceKO("AddBet Fail : Wrong arguments, expecting: login idPool ammount value");
        }
        if(!poolExist(idPool)){
            return serviceKO("AddBet Failed : Pool doesn't exists");
        }
        boolean connected = userConnected(login);
        if (!connected) return serviceKO("AddBet Fail : User not connected");


        if (!SessionTools.checkToken(token, login)) {
            return serviceKO("AddBet Fail : Wrong token");
        }

        try {
            if (!betPoolOpen(idPool)) {
                return serviceKO("AddBet Fail : Too late to add a new bet");
            }
        } catch (SQLException e) {
            return serviceKO("AddBet Fail : SQLException");

        } catch (URISyntaxException e) {
            return serviceKO("AddBet Fail : URISyntaxException");

        }



        if(checkBetExist(login,idPool)) return serviceKO("AddBet Fail : Only one bet allowed by Pool and User");
        if(isSuscribed(login,idPool)) return serviceKO("AddBet Fail : User not subscribed to the pool");
        String query = "SELECT solde FROM USERS WHERE login=?";
        try(Connection c = Database.getConnection();
            PreparedStatement pstmt = c.prepareStatement(query);){
            pstmt.setString(1,login);
            ResultSet result = pstmt.executeQuery();
            result.next();
            int solde = result.getInt(1);
            solde = solde - Integer.parseInt(ammount);
            if(solde<0){
                return serviceKO("AddBet Failed : You don't have enough coin to place this bet");
            }
        }
        if (BetTools.addBet(idPool, login, Integer.parseInt(ammount), Double.parseDouble(value))) {
            return serviceOK();
        }
        return serviceKO("AddBet Fail : BetPool not found");
    }

    public static boolean isSuscribed(String login, String idPool){
        MongoCollection<Document> collection = getMongoCollection("Bet");
        Document d =
                collection
                        .find(and(new BsonDocument().append("idBetPool", new BsonString(idPool)),
                                new BsonDocument().append("gamblerLogin", new BsonString(login))))
                        .first();
        if(d!=null)
            return true;
        return false;
    }

    /* service d'annulation d'un pari */
    public static JSONObject cancelBet(String login, String idPool, String token){

        if((login == null) || (idPool == null) || (token == null)) {
            return serviceKO("CancelBet Fail : Wrong arguments, expecting: login idPool token");
        }

        boolean connected = userConnected(login);
        if(!connected) return serviceKO("CancelBet Fail : User not connected");

        if(!SessionTools.checkToken(token, login)){
            return serviceKO("CancelBet Fail : Wrong token");
        }

        try {
            boolean canCancel = betPoolOpen(idPool);
            if(!canCancel){
                return serviceKO("CancelBet Fail : Too late to cancel");
            }
        } catch (URISyntaxException e) {
            return serviceKO("CancelBet Fail : URISyntaxException");
        } catch (SQLException e) {
            e.printStackTrace();
            return serviceKO("CancelBet Fail : SQLException");
        }

        try {
            if(BetTools.cancelBet(login, idPool)){
                return serviceOK();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return serviceKO("CancelBet Fail : URISyntaxException");
        } catch (SQLException e) {
            e.printStackTrace();
            return serviceKO("CancelBet Fail : SQLException");

        }

        return serviceKO("CancelBet Fail : No such Pool or No bet done");
    }

    public static JSONObject retrieveGain(String login, String idPool) throws URISyntaxException, SQLException {
        if(!checkBetExist(login,idPool)){
            return serviceKO("Gain Retrieval Failed : No bet registered");
        }

        if(BetTools.betWon(login,idPool)){
            MongoCollection<Document> collection = getMongoCollection("Bet");
            Document d =
                    collection
                            .find(and(new BsonDocument().append("idBetPool", new BsonString(idPool)),
                                    new BsonDocument().append("gamblerLogin", new BsonString(login))))
                            .first();
            String amount_s = d.get("betAmount").toString();
            int amount_i = Integer.parseInt(amount_s);
            String query = "SELECT solde FROM USERS WHERE login=?";
            collection = getMongoCollection("L_Bet");
            Document d_tmp =
                    collection
                            .find(new BsonDocument().append("idBetPool", new BsonString(idPool)))
                            .first();
            if(d_tmp.getDouble("resultValue")==null){
                return serviceKO("Gain Retrieval Failed : the pool isn't closed yet.");
            }
            try(Connection c = Database.getConnection();
                PreparedStatement pstmt = c.prepareStatement(query);) {
                pstmt.setString(1, login);
                ResultSet res = pstmt.executeQuery();
                res.next();
                int soldeAccount = res.getInt(1);
                res.close();
                int amountWon ;
                boolean type;
                query = "SELECT pooltype FROM BETPOOL WHERE idbetpool=?";
                try(PreparedStatement pstmt3 = c.prepareStatement(query);){
                    pstmt3.setString(1,idPool);
                    ResultSet restype = pstmt3.executeQuery();
                    type = restype.getBoolean(1);
                    restype.close();
                }
                if(type){
                    amountWon = amount_i *5;
                }else{
                    amountWon = (int) Math.round(amount_i *1.2);
                }
                soldeAccount = soldeAccount + amountWon;
                query = "UPDATE USERS SET solde=? WHERE login=?";
                try (PreparedStatement pstmt2 = c.prepareStatement(query);) {
                    pstmt2.setInt(1, soldeAccount);
                    pstmt2.setString(2, login);
                    pstmt2.executeQuery();
                }
                JSONObject json = serviceOK();

                json.append("Result","You won ! congratulation !");
                json.append("Gain",""+amountWon);
                return json;
            }
        }else{
            JSONObject json = serviceOK();
            json.append("Result","You lost your bet, try again next time");
            return json;

        }
    }
}
