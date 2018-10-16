package services;

import bd.APITools;
import org.json.JSONObject;

import static services.ServiceTools.serviceKO;
import static services.ServiceTools.serviceOK;

public class APIService {

    public static JSONObject getCryptoCurrency(String cryptName, String devise, String fin, String debut){
        try {
            JSONObject json = serviceOK();
            String tmp = APITools.getCrypto(cryptName, devise, fin, debut);
            json.append("results",tmp);
            return json;
        }catch(Exception e){
            return serviceKO("Getting Crypto Currency Failed : IOException");
        }
    }

}
