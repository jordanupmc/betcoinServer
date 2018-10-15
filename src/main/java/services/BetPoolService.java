package services;

import bd.BetTools;
import bd.SessionTools;
import bd.UserTools;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.sql.SQLException;

import static bd.UserTools.userConnected;
import static services.ServiceTools.serviceKO;


public class BetPoolService {
    public static JSONObject getListPoolsActive(){
        JSONObject j=ServiceTools.serviceOK();
        j.put("betpools", BetTools.getListPoolsActive());
        return j;
    }

    public static JSONObject quitPool(String login, String idPool,String token){
        JSONObject obj;

        if((login==null)||(idPool==null)||(token==null)) return serviceKO("QuitPool : Null argument");
        if(!userConnected(login)) return serviceKO("QuitPool Fail : User not connected");

        if(!SessionTools.checkToken(token, login)){
            return serviceKO("QuitPool Fail : Wrong token");
        }

        try {
            if(BetTools.quitPool(login,idPool)) {
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
}
