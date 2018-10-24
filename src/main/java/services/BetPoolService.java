package services;

import bd.BetTools;
import bd.Database;
import bd.PoolTools;
import bd.SessionTools;
import com.mongodb.client.MongoCollection;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static bd.Database.getMongoCollection;
import static bd.PoolTools.isSubscribed;
import static bd.PoolTools.poolExist;
import static bd.SessionTools.userConnected;
import static services.ServiceTools.serviceKO;
import static services.ServiceTools.serviceOK;


public class BetPoolService {

    /*service de récupération de la liste des salons de pari actif */
    public static JSONObject getListPoolsActive() {
        JSONObject j = ServiceTools.serviceOK();
        j.put("betpools", BetTools.getListPoolsActive());
        return j;
    }

    /* service permettant de quitter un salon de pari */
    public static JSONObject quitPool(String login, String idPool, String token) {
        JSONObject obj;

        if ((login == null) || (idPool == null) || (token == null)) return serviceKO("QuitPool Failed : Null argument");
        if (!userConnected(login)) return serviceKO("QuitPool Failed : User not connected");

        if (!SessionTools.checkToken(token, login)) {
            return serviceKO("QuitPool Fail : Wrong token");
        }
        try {
            if (!poolExist(idPool)) {
                return serviceKO("QuitPool Failed : Pool doesn't exists");
            }

            if(!isSubscribed(login,idPool)){
                return serviceKO("QuitPool Failed : You are not subscribed to this pool");
            }
            if (PoolTools.quitPool(login, idPool)) {
                obj = ServiceTools.serviceOK();
                obj.put("login", login);
                obj.put("quittedPool", idPool);
            } else {
                obj = ServiceTools.serviceKO("QuitPool Failed : Couldn't quit pool " + idPool);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return ServiceTools.serviceKO("QuitPool Failed : URISyntaxException");

        } catch (SQLException e) {
            return ServiceTools.serviceKO("QuitPool : SQLException");
        }
        return obj;
    }

    /* service permettant l'entrée dans un salon de pari */
    public static JSONObject enterPool(String login, String idPool, String token) {

        if ((login == null) || (idPool == null) || (token == null)) {
            return serviceKO("EnterPool Fail : Wrong arguments, expecting: login idPool token");
        }

        boolean connected = userConnected(login);
        if (!connected) return serviceKO("EnterPool Fail : User not connected");

        if (!SessionTools.checkToken(token, login)) {
            return serviceKO("EnterPool Fail : Wrong token");
        }

        try {
            if (!poolExist(idPool)) {
                return serviceKO("EnterPool Fail : This pool does not exist");
            }
        } catch (URISyntaxException e) {
            return serviceKO("EnterPool Fail : URISyntaxException");
        } catch (SQLException e) {
            e.printStackTrace();
            return serviceKO("EnterPool Fail : SQLException");
        }

        PoolTools.enterPool(login, idPool);
        return serviceOK();
    }

    /* service permettant de laisser un message sur un salon de pari */
    public static JSONObject messagePool(String login, String idPool, String token, String message) {

        if ((login == null) || (idPool == null) || (token == null) || (message == null)) {
            return serviceKO("MessagePool Fail : Wrong arguments, expecting: login idPool token message");
        }

        if (message.isEmpty()) {
            return serviceKO("MessagePool Fail : Can't post a empty message");
        }

        boolean connected = userConnected(login);
        if (!connected) return serviceKO("MessagePool Fail : User not connected");

        if (!SessionTools.checkToken(token, login)) {
            return serviceKO("MessagePool Fail : Wrong token");
        }

        try {
            if (!poolExist(idPool)) {
                return serviceKO("MessagePool Fail : This pool does not exist");
            }
        } catch (URISyntaxException e) {
            return serviceKO("MessagePool Fail : URISyntaxException");
        } catch (SQLException e) {
            e.printStackTrace();
            return serviceKO("MessagePool Fail : SQLException");

        }

        PoolTools.messagePool(login, idPool, message);
        return serviceOK();
    }

    /*permet de visualiser les informations relatives à une pool*/
    public static JSONObject visualisePool(String idPool) {
        JSONObject json = new JSONObject();
        try {
            if (!poolExist(idPool)) {
                return serviceKO("Visualise Pool Failed : Pool doesn't exists");
            }

            json = PoolTools.poolInfo(idPool);
        }catch(URISyntaxException e){
            json = serviceKO("Visualise Pool Failed : URISyntaxException");
        }catch(SQLException e){
            json = serviceKO("Visualise Pool Failed : SQLException");
        }
        return json;
    }
}
