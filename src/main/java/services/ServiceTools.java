
package services;

import org.json.JSONException;
import org.json.JSONObject;

public class ServiceTools {

    public static JSONObject serviceKO(String str) {
        try{
            JSONObject json = new JSONObject();
            json.put("status", "KO");
            json.put("message", str);
            return json;
        }catch(JSONException je){
            je.printStackTrace();
            return null;
        }
    }

    public static JSONObject serviceOK(){
        JSONObject json = new JSONObject();
        json.put("status", "OK");
        return json;
    }
}