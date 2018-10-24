package bd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;



public class APITools {
    private static String createURL(String cryptName, String devise, String fin, String debut,int isHours){
        long tmsp_fin = Long.parseLong(fin)*1000;
        long tmsp_debut = Long.parseLong(debut)*1000;
        long diff = Math.abs(tmsp_fin - tmsp_debut);
        long diffInHours = (diff / (60*60*1000))%24;
        long diffInDays = Math.round(diff / (60*60*1000)/24);
        long diffInMinute = Math.round(diff / (60*1000));

        long diffTot ;
        if(isHours==1) {
            diffTot = 24 * diffInDays + diffInHours;
        }else{
            diffTot = diffInMinute;
        }
        String retour = "https://min-api.cryptocompare.com/data/" +
                (isHours==1 ? "histohour" : "histominute") +
                "?fsym=" +
                cryptName +
                "&tsym=" +
                devise +
                "&limit=" +
                diffTot +
                "&toTs=" +
                tmsp_fin;
        return retour;
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

        while ((inputLine = in.readLine()) != null)
            source +=inputLine;
        in.close();
        return source;
    }
}
