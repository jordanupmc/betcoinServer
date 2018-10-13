package services;

import bd.BetTools;
import bd.UserTools;
import org.json.JSONObject;


public class BetPoolService {
    public static JSONObject getListPoolsActive(){
        JSONObject j=new JSONObject();
        j.put("status", "OK");
        j.put("betpools", BetTools.getListPoolsActive());
        return j;
    }

    public static JSONObject quitPool(String login, String idPool){
        JSONObject obj = new JSONObject();
        if(BetTools.quitPool(login,idPool)) {
            obj.put("status", "OK");
            obj.put("poolQuitted", idPool);
            obj.put("by",login);
        }else{
            obj.put("status", "KO");
            obj.put("errMSg","couldn't quit pool "+idPool);
        }
        return obj;
    }
}
