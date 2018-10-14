package services;

import bd.BetTools;
import bd.UserTools;
import org.json.JSONObject;


public class BetPoolService {
    public static JSONObject getListPoolsActive(){
        JSONObject j=ServiceTools.serviceOK();
        j.put("betpools", BetTools.getListPoolsActive());
        return j;
    }

    public static JSONObject quitPool(String login, String idPool){
        JSONObject obj;
        if(BetTools.quitPool(login,idPool)) {
            obj = ServiceTools.serviceOK();
            obj.put("poolQuitted", idPool);
            obj.put("by",login);
        }else{
            obj = ServiceTools.serviceKO("couldn't quit pool "+idPool);
        }
        return obj;
    }
}
