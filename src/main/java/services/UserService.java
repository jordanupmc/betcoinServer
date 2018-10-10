package services;

import bd.SessionTools;
import bd.UserTools;
import org.json.JSONObject;

import java.sql.Date;

public class UserService {
    /*Login*Mdp*ConfirmMdp*Email*Nom*Prenom*DateNaissance(dd/mm/aaaa)*Pays->void
     */
    //TODO check ce mettre d'accord sur le type de date a utiliser
    public static JSONObject subscribe(String login, String mdp, String cmdp ,String email, String nom, String prenom,  Date birthDate, String country){
        JSONObject j=new JSONObject();
        if(cmdp.equals(mdp) && UserTools.subscribe(login, mdp, email, nom, prenom, birthDate, country)){
            j.put("status", "OK");
        }
        else{
            j.put("status", "KO");
            j.put("errorMsg", "Subscribe fail");
        }
        return j;
    }

    /*TODO que faire apres la suppression en base ? supprimer l'obet dans la collection Session ou bien mettre isConnected a false ?*/
    public static JSONObject unsubscribe(String login, String token){
        JSONObject j=new JSONObject();

        if(SessionTools.checkToken(token, login) && UserTools.unsubscribe(login)){
            j.put("status", "OK");
        }else{
            j.put("status", "KO");
            j.put("errorMsg", "Unsubscribe fail");
        }

        return j;
    }
}
