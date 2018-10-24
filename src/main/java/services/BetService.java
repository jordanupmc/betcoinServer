package services;

import bd.BetTools;
import bd.Database;
import bd.SessionTools;
import com.mongodb.client.MongoCollection;
import com.mongodb.util.JSON;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static bd.BetTools.*;
import static bd.Database.getMongoCollection;
import static bd.PoolTools.poolExist;
import static bd.SessionTools.checkToken;
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
        if (!poolExist(idPool)) {
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


        if (checkBetExist(login, idPool)) return serviceKO("AddBet Fail : Only one bet allowed by Pool and User");
        if (!isSubscribed(login, idPool)) return serviceKO("AddBet Fail : User not subscribed to the pool");
        if(!hasEnoughCoin(login,ammount)){
            return serviceKO("AddBet Failed : You don't have enough coin to place this bet");
        }
        if (BetTools.addBet(idPool, login, Integer.parseInt(ammount), Double.parseDouble(value))) {
            return serviceOK();
        }
        return serviceKO("AddBet Fail : BetPool not found");
    }


    /* service d'annulation d'un pari */
    public static JSONObject cancelBet(String login, String idPool, String token) {

        if ((login == null) || (idPool == null) || (token == null)) {
            return serviceKO("CancelBet Fail : Wrong arguments, expecting: login idPool token");
        }

        boolean connected = userConnected(login);
        if (!connected) return serviceKO("CancelBet Fail : User not connected");

        if (!SessionTools.checkToken(token, login)) {
            return serviceKO("CancelBet Fail : Wrong token");
        }

        try {
            boolean canCancel = betPoolOpen(idPool);
            if (!canCancel) {
                return serviceKO("CancelBet Fail : Too late to cancel");
            }
        } catch (URISyntaxException e) {
            return serviceKO("CancelBet Fail : URISyntaxException");
        } catch (SQLException e) {
            e.printStackTrace();
            return serviceKO("CancelBet Fail : SQLException");
        }

        try {
            if (BetTools.cancelBet(login, idPool)) {
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
        if (!checkBetExist(login, idPool)) {
            return serviceKO("Gain Retrieval Failed : No bet registered");
        }
        if(!checkPoolResult(idPool)){
            return serviceKO("Gain Retrieval Failed : the pool isn't closed yet.");
        }
        int amountWon;
        if ((amountWon = BetTools.betWon(login, idPool)) != -1) {
            JSONObject json = serviceOK();
            json.append("Result", "You won ! congratulation !");
            json.append("Gain", "" + amountWon);
            return json;

        } else {
            JSONObject json = serviceOK();
            json.append("Result", "You lost your bet, try again next time");
            return json;

        }
    }

    public static JSONObject hasBet(String login, String idPool, String token){
        JSONObject json ;
        if(!checkToken(token,login)){
            serviceKO("HasBet Failed : Wrong token");
        }
        if(!checkBetExist(login,idPool)){
            json = serviceOK();
            json.append("result",false);
            return json;
        }
        json = serviceOK();
        json.append("result",true);
        return json;
    }

    public static JSONObject getListBets(String login) {
        JSONObject json ;
        json = serviceOK();
        json.put("bets",BetTools.getListBets(login));
        return json;
    }

    public static JSONObject getBet(String login, String idPool){
        JSONObject json ;
        json = serviceOK();
        json.append("bet",BetTools.getBet(login,idPool));
        return json;
    }
}
