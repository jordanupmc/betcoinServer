package services;

import bd.BetTools;

import bd.PoolTools;
import bd.SessionTools;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;

import java.sql.SQLException;


import static bd.BetTools.checkBetExist;
import static bd.PoolTools.isSubscribed;
import static bd.PoolTools.poolExist;
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

        if (!userConnected(login)) return serviceKO("You are not connected", true);

        if (!SessionTools.checkToken(token, login)) {
            return serviceKO("Please, login once again", true);
        }
        try {
            if (!poolExist(idPool)) {
                return serviceKO("This pool doesn't exists", false);
            }

            if(!isSubscribed(login,idPool)){
                return serviceKO("You are not subscribed to this pool, so you can't unsubscribe to it", false);
            }

            if (checkBetExist(login, idPool)) return serviceKO("Please cancel your bet before leaving the pool", false);



            if (PoolTools.quitPool(login, idPool)) {
                obj = ServiceTools.serviceOK();
                obj.put("login", login);
                obj.put("quittedPool", idPool);
            } else {
                obj = ServiceTools.serviceKO("Couldn't quit pool " + idPool, false);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return ServiceTools.serviceKO("QuitPool Failed", false);

        } catch (SQLException e) {
            return ServiceTools.serviceKO("QuitPool Failed:", false);
        }
        return obj;
    }

    /* service permettant l'entrée dans un salon de pari */
    public static JSONObject enterPool(String login, String idPool, String token) {

        boolean connected = userConnected(login);
        if (!connected) return serviceKO("You are not connected", true);

        if (!SessionTools.checkToken(token, login)) {
            return serviceKO("Please, log once again", true);
        }

        try {
            if (!poolExist(idPool)) {
                return serviceKO("This pool does not exist", false);
            }
        } catch (URISyntaxException e) {
            return serviceKO("EnterPool Fail", false);
        } catch (SQLException e) {
            e.printStackTrace();
            return serviceKO("EnterPool Fail", false);
        }

        PoolTools.enterPool(login, idPool);
        return serviceOK();
    }

    /* service permettant de laisser un message sur un salon de pari */
    public static JSONObject messagePool(String login, String idPool, String token, String message) {



        boolean connected = userConnected(login);
        if (!connected) return serviceKO("You are not connected", true);

        if (!SessionTools.checkToken(token, login)) {
            return serviceKO("Please, log once again", true);}

        if (message.isEmpty()) {
            return serviceKO("It's not possible to post an empty message", false);
        }

        try {
            if (!poolExist(idPool)) {
                return serviceKO("This pool does not exist", false);
            }
        } catch (URISyntaxException e) {
            return serviceKO("MessagePool Fail", false);
        } catch (SQLException e) {
            e.printStackTrace();
            return serviceKO("MessagePool Fail", false);

        }

        PoolTools.messagePool(login, idPool, message);
        return serviceOK();
    }

    /*permet de visualiser les informations relatives à une pool*/
    public static JSONObject visualisePool(String idPool) {
        JSONObject json;
        try {
            if (!poolExist(idPool)) {
                return serviceKO("This pool doesn't exists", false);
            }

            json = PoolTools.poolInfo(idPool);
        }catch(URISyntaxException e){
            json = serviceKO("Visualise Pool Failed", false);
        }catch(SQLException e){
            json = serviceKO("Visualise Pool Failed", false);
        }
        return json;
    }


    public static JSONObject getListMessagePool(String login, String token, int idPool){
        JSONObject json;
        JSONArray arr;

        if (!SessionTools.checkToken(token, login)) {
            return serviceKO("Please, log once again", true);
        }


        if((arr = PoolTools.getListMessagePool(idPool)) !=null){
            json = serviceOK();
            json.put("messages", arr);
            return json;
        }else
            return serviceKO("This pool doesn't exists", false);


    }

    public static JSONObject getListMessagePool(String login, String token, int idPool, String fromId){
        JSONObject json;
        JSONArray arr;
        JSONArray setMail;
        if(checkToken(token, login)){
            if((arr = PoolTools.getListMessagePool(idPool, fromId)) !=null){
                json = serviceOK();
                json.put("messages", arr);
                try {
                    if( (setMail = PoolTools.getEnsembleUrlChat(arr)) != null)
                        json.put("setMail", setMail);
                } catch (URISyntaxException | SQLException e) {
                    return serviceKO("Echec recuperation des mails");
                }
                return json;
            }
            else
                return serviceKO("This pool doesn't exists", false);
        }
        else
            return serviceKO("Please, log once again", true);
    }

    public static JSONObject deleteMessage(String login, String idPool, String token, String msgId) {
        if(!checkToken(token, login)) return serviceKO("Please, log once again", true);
        try {
            if (!poolExist(idPool)) return serviceKO("This pool doesn't exists", false);

            if(PoolTools.deleteMessage(idPool, msgId)) {
                return serviceOK();
            }
            return serviceKO("Delete message failed", false);
        }catch (Exception sq){
            return serviceKO("DeleteMessage  Failed", false);
        }


    }
}
