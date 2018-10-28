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

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static bd.BetTools.*;
import static bd.Database.getMongoCollection;
import static bd.PoolTools.isSubscribed;
import static bd.PoolTools.poolExist;
import static bd.SessionTools.checkToken;
import static bd.SessionTools.userConnected;
import static com.mongodb.client.model.Filters.and;
import static services.ServiceTools.serviceKO;
import static services.ServiceTools.serviceOK;

public class BetService {

    /* service pour l'ajout d'un pari par un utilisateur */
    public static JSONObject addBet(String token, String login, String idPool, String ammount, String value) throws URISyntaxException, SQLException {

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
            }else{
                return serviceKO("CancelBet Fail : No such Pool or No bet done");
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
            return serviceKO("CancelBet Fail : URISyntaxException");
        } catch (SQLException e) {
            e.printStackTrace();
            return serviceKO("CancelBet Fail : SQLException");

        }

    }

    public static JSONObject retrieveGain(String login, String token, String idPool) {

        boolean connected = userConnected(login);
        if (!connected) return serviceKO("CancelBet Fail : User not connected");

        if (!SessionTools.checkToken(token, login)) {
            return serviceKO("CancelBet Fail : Wrong token");
        }

        if (!checkBetExist(login, idPool)) {
            return serviceKO("Gain Retrieval Failed : No bet registered");
        }
        try {
            if(!betResultIsAvailable(idPool)){
                return serviceKO("Gain Retrieval Failed : the pool isn't closed yet.");
            }
            if(haveCheckResultAlready(login, idPool)){
                return serviceKO("Gain Retrieval Failed : You have already check this bet");
            }
            int amountWon;
            if ((amountWon = BetTools.betWon(login, idPool)) > 0) {
                JSONObject json = serviceOK();
                json.append("Result", "You won ! congratulation !");
                json.append("Gain", "" + amountWon);
                return json;

            } else if(amountWon == 0){
                JSONObject json = serviceOK();
                json.append("Result", "You lost your bet, try again next time");
                return json;
            }else{
                return serviceKO("Gain Retrieval Failed : -1");
            }
        } catch (URISyntaxException e) {
            return serviceKO("Gain Retrieval Failed : URISyntaxException");
        } catch (SQLException e) {
            return serviceKO("Gain Retrieval Failed : SQLException");
        }catch (IOException e) {
            return serviceKO("Gain Retrieval Failed : IOException");
        }
    }

    public static JSONObject hasBet(String login, String idPool, String token){
        JSONObject json ;
        if(!checkToken(token,login)){
            return serviceKO("HasBet Failed : Wrong token");
        }
        if(!checkBetExist(login,idPool)){
            json = serviceOK();
            json.put("result",false);
            return json;
        }
        json = serviceOK();
        json.put("result",true);
        return json;
    }

    public static JSONObject getListBets(String login, String token) {
        if(!checkToken(token,login)){
            return serviceKO("GetListBets Failed : Wrong token");
        }
        JSONObject json ;
        json = serviceOK();
        json.put("bets",BetTools.getListBets(login));
        return json;
    }

    public static JSONObject getBet(String login, String token,String idPool){

        if(!checkToken(token,login)){
            return serviceKO("GetBet Failed : Wrong token");
        }

        JSONObject json ;
        json = serviceOK();
        json.append("bet",BetTools.getBet(login,idPool));
        return json;
    }

    public static JSONObject betResultAvailable(String login, String idPool, String token) {
        if(!checkToken(token,login)){
            return serviceKO("BetResultAvailable Failed : Wrong token");
        }
        try {
            if(betResultIsAvailable(idPool) && !haveCheckResultAlready(login, idPool)){
                JSONObject json = serviceOK();
                json.put("result",true);
                return json;
            }
        } catch (URISyntaxException e) {
            return serviceKO("BetResultAvailable Fail : URISyntaxException");
        } catch (SQLException e) {
            return serviceKO("BetResultAvailable Fail : SQLException");
        }
        JSONObject json = serviceOK();
        json.put("result",false);
        return json;
    }
}
