package services;

import bd.SessionTools;
import bd.UserTools;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.sql.Date;
import java.sql.SQLException;

import static bd.UserTools.checkPasswd;
import static services.ServiceTools.serviceKO;
import static services.ServiceTools.serviceOK;

public class UserService {
    /*Login*Mdp*ConfirmMdp*Email*Nom*Prenom*DateNaissance(dd/mm/aaaa)*Pays->void
     */
    //TODO check ce mettre d'accord sur le type de date a utiliser
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

    public static JSONObject unsubscribe(String login, String token){
        JSONObject j;

        if(SessionTools.checkToken(token, login) && UserTools.unsubscribe(login)){
            j = serviceOK();
        }else{
            j = serviceKO( "Unsubscribe fail");
        }

        return j;
    }

    public static JSONObject disconnect(String login, String token){
        JSONObject json;

        if(SessionTools.checkToken(token, login)){
            UserTools.disconnect(login,token);
            json = serviceOK();
            json.put("disconnectedLogin",login);
        }else{
            json = serviceKO("Already disconnected");
        }
        return json;
    }

    public static JSONObject visualiseAcc(String login){
        JSONObject json;
        json = UserTools.visualiseAccount(login);
        if(json!=null){
            json.put("status","OK");
        }else{
            json = serviceKO("VisualiseAccount Failed : couldn't retrieve the informations");
        }
        return json;
    }

    public static JSONObject changeFieldAccount(String login, String pwd, String field_name, String new_value){
        JSONObject json;
        try {
            if (!checkPasswd(login, pwd)) {
                return serviceKO("AccountModification Failed : Wrong password");
            }
        }catch(SQLException e){
            return serviceKO("AccountModification Failed : SQLException ");
        }catch (URISyntaxException e){
            return serviceKO("AccountModification Failed : URISyntaxException ");
        }
        if(UserTools.accountModification(login,field_name,new_value)){
            json = serviceOK();
        }else{
            json = serviceKO("couldn't change your account's information");
        }

        return json;
    }
}
