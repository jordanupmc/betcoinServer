package services;

import bd.SessionTools;
import bd.UserTools;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.sql.Date;
import java.sql.SQLException;

import static services.ServiceTools.serviceKO;
import static services.ServiceTools.serviceOK;

public class UserService {
    /*Login*Mdp*ConfirmMdp*Email*Nom*Prenom*DateNaissance(dd/mm/aaaa)*Pays->void
     */
    /* service d'inscription d'un utilisateur */
    public static JSONObject subscribe(String login, String mdp, String cmdp ,String email, String nom, String prenom,  Date birthDate, String country){
        JSONObject j;
        try {
            if(cmdp.equals(mdp) && UserTools.subscribe(login, mdp, email, nom, prenom, birthDate, country)){
                j = serviceOK();
            }
            else{
               j= serviceKO("Subscribe fail");
               if(!cmdp.equals(mdp))
                    j= ServiceTools.serviceKO("Subscribe fail two different password");
            }
        } catch (SQLException e) {
            j= serviceKO(e.getMessage());
        }
        return j;
    }

    /* service de désinscription d'un utilisateur */
    public static JSONObject unsubscribe(String login, String token, String password){
        JSONObject j;

        try {
            if(SessionTools.checkToken(token, login) && UserTools.checkPasswd(login, password) && UserTools.unsubscribe(login)){
                j = serviceOK();
            }else{
                j = serviceKO( "Unsubscribe fail");
            }
        } catch (Exception e) {
            j = serviceKO( "Unsubscribe fail");
        }

        return j;
    }


    public static JSONObject getUserInfo(String login, String token){
        JSONObject j;

        if(SessionTools.checkToken(token, login)) {

            JSONObject tmp = UserTools.getUserInfo(login);
            if(!tmp.isEmpty()) {
                j = serviceOK();
                j.put("user", tmp);
                return j;
            }
            else
                return serviceKO("Erreur lors de la récupération des infos");
        }
        return serviceKO("Permission denied for "+login + " account");

    }
}
