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
}
