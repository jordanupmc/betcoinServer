package services;

import bd.BetTools;

import bd.PoolTools;
import bd.SessionTools;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;

import java.sql.SQLException;


import static bd.PoolTools.isSubscribed;
import static bd.PoolTools.poolExist;
import static bd.PoolTools.poolInfo;
import static bd.SessionTools.checkToken;
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
            return ServiceTools.serviceKO("QuitPool Failed: SQLException");
        }
        return obj;
    }

    /* service permettant l'entrée dans un salon de pari */
    public static JSONObject enterPool(String login, String idPool, String token) {

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
        JSONObject json;
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


    public static JSONObject getListMessagePool(String login, String token, int idPool){
        JSONObject json;
        JSONArray arr;
        if(checkToken(token, login)){
            if((arr = PoolTools.getListMessagePool(idPool)) !=null){
                json = serviceOK();
                json.put("messages", arr);
                return json;
            }
            else
                return serviceKO("getListMessagePool : La pool n'existe pas");
        }
        else
            return serviceKO("getListMessagePool : "+login+" n'est pas connecté !" );
    }

    public static JSONObject getListMessagePool(String login, String token, int idPool, String fromId){
        JSONObject json;
        JSONArray arr;
        if(checkToken(token, login)){
            if((arr = PoolTools.getListMessagePool(idPool, fromId)) !=null){
                json = serviceOK();
                json.put("messages", arr);
                return json;
            }
            else
                return serviceKO("getListMessagePool : La pool n'existe pas");
        }
        else
            return serviceKO("getListMessagePool : "+login+" n'est pas connecté !" );
    }
}
