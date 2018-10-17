package services;

import bd.BetTools;
import bd.PoolTools;
import bd.SessionTools;
import org.json.JSONObject;
import java.net.URISyntaxException;
import java.sql.SQLException;

import static bd.SessionTools.userConnected;
import static services.ServiceTools.serviceKO;
import static services.ServiceTools.serviceOK;


public class BetPoolService {

    /*service de récupération de la liste des salons de pari actif */
    public static JSONObject getListPoolsActive(){
        JSONObject j=ServiceTools.serviceOK();
        j.put("betpools", BetTools.getListPoolsActive());
        return j;
    }

    /* service permettant de quitter un salon de pari */
    public static JSONObject quitPool(String login, String idPool,String token){
        JSONObject obj;

        if((login==null)||(idPool==null)||(token==null)) return serviceKO("QuitPool : Null argument");
        if(!userConnected(login)) return serviceKO("QuitPool Fail : User not connected");

        if(!SessionTools.checkToken(token, login)){
            return serviceKO("QuitPool Fail : Wrong token");
        }

        try {
            if(PoolTools.quitPool(login,idPool)) {
                obj = ServiceTools.serviceOK();
                obj.put("login",login);
                obj.put("quittedPool", idPool);
            }else{
                obj = ServiceTools.serviceKO("couldn't quit pool "+idPool);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return ServiceTools.serviceKO("QuitPool : URISyntaxException");

        } catch (SQLException e) {
            return ServiceTools.serviceKO("QuitPool : SQLException");
        }
        return obj;
    }

    /* service permettant l'entrée dans un salon de pari */
    public static JSONObject enterPool(String login, String idPool, String token){

        if((login == null) || (idPool == null) || (token == null)) {
            return serviceKO("EnterPool Fail : Wrong arguments, expecting: login idPool token");
        }

        boolean connected = userConnected(login);
        if(!connected) return serviceKO("EnterPool Fail : User not connected");

        if(!SessionTools.checkToken(token, login)){
            return serviceKO("EnterPool Fail : Wrong token");
        }

        try {
            if(!PoolTools.poolExist(idPool)){
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
    public static JSONObject messagePool(String login, String idPool, String token, String message){

        if((login == null) || (idPool == null) || (token == null) || (message == null)) {
            return serviceKO("MessagePool Fail : Wrong arguments, expecting: login idPool token message");
        }

        if(message.isEmpty()){
            return serviceKO("MessagePool Fail : Can't post a empty message");
        }

        boolean connected = userConnected(login);
        if(!connected) return serviceKO("MessagePool Fail : User not connected");

        if(!SessionTools.checkToken(token, login)){
            return serviceKO("MessagePool Fail : Wrong token");
        }

        try {
            if(!PoolTools.poolExist(idPool)){
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
}
