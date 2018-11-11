package services;

import bd.APITools;
import org.bson.Document;
import org.json.JSONObject;
import static services.ServiceTools.serviceKO;
import static services.ServiceTools.serviceOK;

public class APIService {

    public static JSONObject getCryptoCurrency(String cryptName, String devise, String fin, String debut,int isHours){
        try {
            JSONObject json = serviceOK();
            String tmp = APITools.getCrypto(cryptName, devise, fin, debut, isHours);
            Document doc = new Document(Document.parse(tmp));
            String resp = doc.get("Response").toString();
            if(resp.equals("Error")){
                return serviceKO(doc.get("Message").toString(), false);
            }
            json.append("results",doc);
            return json;
        }catch(Exception e){
            return serviceKO("Getting Crypto Currency Failed", false);
        }
    }

}
