package bd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class APITools {
    private static String createURL(String cryptName, String devise, String tmstp, String limit){
        String retour = "https://min-api.cryptocompare.com/data/histohour?fsym=" +
                cryptName +
                "&tsym=" +
                devise +
                "&limit=" +
                limit +
                "&toTs=" +
                tmstp;

        return retour;
    }

    public static String getCrypto(String cryptName,String devise, String tmstp, String limit) throws IOException {
        String source ="";
        String url = createURL(cryptName,devise,tmstp,limit);
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
