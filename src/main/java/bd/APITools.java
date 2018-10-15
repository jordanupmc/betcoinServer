package bd;

import com.mongodb.util.JSON;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;


public class APITools {
    private static String createURL(String cryptName, String devise, String fin, String debut){
        Timestamp tmstp_fin = new Timestamp(Timestamp.parse(fin));
        Timestamp tmstp_debut = new Timestamp(Timestamp.parse(debut));

        String retour = "https://min-api.cryptocompare.com/data/histohour?fsym=" +
                cryptName +
                "&tsym=" +
                devise +
                "&limit=" +
                fin +
                "&toTs=" +
                "";

        return retour;
    }

    public static JSONObject tCrypto(String cryptName, String devise, String fin, String debut) throws IOException {
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
        return new JSONObject(source);
    }
}
