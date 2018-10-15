package services;

import bd.SessionTools;
import bd.UserTools;
import org.json.JSONObject;

import java.sql.Date;

import static services.ServiceTools.serviceKO;
import static services.ServiceTools.serviceOK;

public class UserService {
    /*Login*Mdp*ConfirmMdp*Email*Nom*Prenom*DateNaissance(dd/mm/aaaa)*Pays->void
     */
    //TODO check ce mettre d'accord sur le type de date a utiliser
    /* service d'inscription d'un utilisateur */
    public static JSONObject subscribe(String login, String mdp, String cmdp ,String email, String nom, String prenom,  Date birthDate, String country){
        JSONObject j;
        if(cmdp.equals(mdp) && UserTools.subscribe(login, mdp, email, nom, prenom, birthDate, country)){
            j = serviceOK();
        }
        else{
           j= serviceKO("Subscribe fail");
            if(!cmdp.equals(mdp))
                j= ServiceTools.serviceKO("Subscribe fail two different password");

            else
                j= ServiceTools.serviceKO("Subscribe fail");
        }
        return j;
    }

    /* service de désinscription d'un utilisateur */
    public static JSONObject unsubscribe(String login, String token){
        JSONObject j;

        if(SessionTools.checkToken(token, login) && UserTools.unsubscribe(login)){
            j = serviceOK();
        }else{
            j = serviceKO( "Unsubscribe fail");
        }

        return j;
    }
}
