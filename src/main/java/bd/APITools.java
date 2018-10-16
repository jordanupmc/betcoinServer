package bd;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.Date;


public class APITools {
    private static String createURL(String cryptName, String devise, String fin, String debut){
        Timestamp tmstp_fin = new Timestamp(Long.parseLong(fin));
        Timestamp tmstp_debut = new Timestamp(Long.parseLong(debut));


        Date d1 = new Date(tmstp_fin.getTime());
        Date d2 = new Date(tmstp_debut.getTime());
        int diffInHours = (int)(Math.abs(d1.getTime() - d2.getTime())/3600000);
        JOptionPane.showMessageDialog(null,diffInHours);

        String retour = "https://min-api.cryptocompare.com/data/histohour?fsym=" +
                cryptName +
                "&tsym=" +
                devise +
                "&limit=" +
                diffInHours +
                "&toTs=" +
                tmstp_fin;

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
