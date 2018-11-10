package bd;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class APITools {
    private static String createURL(String cryptName, String devise, String fin, String debut,int isHours){
        long tmsp_fin = Long.parseLong(fin);
        long tmsp_debut = Long.parseLong(debut);
        long diff = Math.abs(tmsp_fin - tmsp_debut);
        long diffInHours = (diff / (60*60))%24;
        long diffInDays = Math.round(diff / (60*60)/24);
        long diffInMinute = Math.round(diff / (60));

        long diffTot ;
        if(isHours==1) {
            diffTot = 24 * diffInDays + diffInHours;
        }else{
            diffTot = diffInMinute;
        }
        return "https://min-api.cryptocompare.com/data/" +
                (isHours==1 ? "histohour" : "histominute") +
                "?fsym=" +
                cryptName +
                "&tsym=" +
                devise +
                "&limit=" +
                diffTot +
                "&toTs=" +
                tmsp_fin;
    }

    /*recupere les données de la cryptomonaie. isHours =1 signifie la valeur toute les heure, minute sinon*/
    public static String getCrypto(String cryptName, String devise, String fin, String debut, int isHours) throws IOException {
        String source ="";
        String url = createURL(cryptName,devise,fin,debut,isHours);
        URL oracle = new URL(url);
        URLConnection yc = oracle.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        yc.getInputStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            source += inputLine;
        }
        in.close();
        return source;
    }



    public static double getPriceSpecificTime(String cryptName, String devise, String time) throws IOException {
        String source ="";
        String url = createURL(cryptName,devise,time,time,1);
        URL oracle = new URL(url);
        URLConnection yc = oracle.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        yc.getInputStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            source +=inputLine;
        in.close();

        double value = 0.0;
        JSONObject obj = new JSONObject(source);
        JSONArray array = obj.getJSONArray("Data");
        for(int i =0; i<array.length(); i++){
            if(array.getJSONObject(i).get("time").toString().equals(time)) {
                return array.getJSONObject(i).getDouble("close");
            }
        }
        return 0;

    }
}
