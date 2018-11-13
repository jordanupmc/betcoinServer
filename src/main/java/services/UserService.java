package services;

import bd.SessionTools;
import bd.UserTools;
import org.json.JSONObject;

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
            if(!cmdp.equals(mdp)){
                return serviceKO("The two passwords are different");
            }
            if(UserTools.subscribe(login, mdp, email, nom, prenom, birthDate, country)){
                j = serviceOK();
            }
            else{
               j= serviceKO("Subscribe fail");
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
            if(! SessionTools.checkToken(token, login))
                return serviceKO("Please, log again", true);

            if(!UserTools.checkPasswd(login, password))
                return serviceKO("Wrong Password");

            if(UserTools.unsubscribe(login)){
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
                return serviceKO("We couldn't retrive the information about your account");
        }
        return serviceKO("Permission denied for "+login + " account");

    }
}
