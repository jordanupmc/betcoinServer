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
        JSONObject j;
        if(cmdp.equals(mdp) && UserTools.subscribe(login, mdp, email, nom, prenom, birthDate, country)){
            j = ServiceTools.serviceOK();
        }
        else{
           j= ServiceTools.serviceKO("Subscribe fail");
        }
        return j;
    }

    public static JSONObject unsubscribe(String login, String token){
        JSONObject j;

        if(SessionTools.checkToken(token, login) && UserTools.unsubscribe(login)){
            j = ServiceTools.serviceOK();
        }else{
            j = ServiceTools.serviceKO( "Unsubscribe fail");
        }

        return j;
    }

    public static JSONObject disconnect(String login, String token){
        JSONObject json;

        if(SessionTools.checkToken(token, login)){
            UserTools.disconnect(login,token);
            json = ServiceTools.serviceOK();
            json.put("disconnectedLogin",login);
        }else{
            json = ServiceTools.serviceKO("Already disconnected");
        }
        return json;
    }

    public static JSONObject visualiseAcc(String login){
        JSONObject json;
        json = UserTools.visualiseAccount(login);
        if(json!=null){
            json.put("status","OK");
        }else{
            json.put("status","KO");
            json.put("message","couldn't retrieve the informations");
        }
        return json;
    }
}
