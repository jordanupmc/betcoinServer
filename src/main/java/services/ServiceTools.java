
package services;

import org.json.JSONException;
import org.json.JSONObject;

public class ServiceTools {
    /* renvois un json pour un service KO */
    public static JSONObject serviceKO(String str) {
        try{
            JSONObject json = new JSONObject();
            json.put("status", "KO");
            json.put("errorMessage", str);
            return json;
        }catch(JSONException je){
            je.printStackTrace();
            return null;
        }
    }

    /* renvois un json pour un service OK */
    public static JSONObject serviceOK(){
        JSONObject json = new JSONObject();
        json.put("status", "OK");
        return json;
    }
}