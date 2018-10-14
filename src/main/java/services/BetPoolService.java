package services;

import bd.BetTools;
import bd.UserTools;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.sql.SQLException;


public class BetPoolService {
    public static JSONObject getListPoolsActive(){
        JSONObject j=ServiceTools.serviceOK();
        j.put("betpools", BetTools.getListPoolsActive());
        return j;
    }

    public static JSONObject quitPool(String login, String idPool){
        JSONObject obj;
        try {
            if(BetTools.quitPool(login,idPool)) {
                obj = ServiceTools.serviceOK();
                obj.put("poolQuitted", idPool);
                obj.put("by",login);
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
