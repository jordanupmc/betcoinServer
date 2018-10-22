package bd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;



public class APITools {
    private static String createURL(String cryptName, String devise, String fin, String debut){
        long tmsp_fin = Long.parseLong(fin)*1000;
        long tmsp_debut = Long.parseLong(debut)*1000;
        long diff = tmsp_fin - tmsp_debut;
        long diffInHours = Math.abs((diff / (60*60*1000))%24);
        long diffInDays = Math.round(diff / (60*60*1000)/24);
        long diffTot = 24*diffInDays + diffInHours;

        String retour = "https://min-api.cryptocompare.com/data/histohour?fsym=" +
                cryptName +
                "&tsym=" +
                devise +
                "&limit=" +
                diffTot +
                "&toTs=" +
                tmsp_fin;
        return retour;
    }

    public static String getCrypto(String cryptName, String devise, String fin, String debut) throws IOException {
        String source ="";
        String url = createURL(cryptName,devise,fin,debut);
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
