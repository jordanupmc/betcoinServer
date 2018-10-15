package services;

import bd.SessionTools;
import bd.UserTools;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.sql.Date;
import java.sql.SQLException;

import static bd.UserTools.checkPasswd;
import static bd.UserTools.userConnected;
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

    /* service de deconnexion d'un utilisateur */
    public static JSONObject disconnect(String login, String token){
        JSONObject json;


        if(!userConnected(login)) return serviceKO("Disconnection Fail : User not connected");

        if(SessionTools.checkToken(token, login)){
            UserTools.disconnect(login,token);
            json = serviceOK();
            json.put("disconnectedLogin",login);
        }else{
            json = serviceKO("Disconnection Fail : Wrong token");
        }
        return json;
    }


    /* service pour la visualisation des informations d'un compte utilisateur*/
    public static JSONObject visualiseAcc(String login,String token){
        JSONObject json;
        if((login == null)||(token == null)) return serviceKO("VisualiseAccount Fail : Null Parameter");
        json = UserTools.visualiseAccount(login);
        boolean connected = userConnected(login);
        if(!connected) return serviceKO("VisualiseAccount Fail : User not connected");

        if(!SessionTools.checkToken(token, login)){
            return serviceKO("VisualiseAccount Fail : Wrong token");
        }
        if(json!=null){
            json.put("status","OK");
        }else{
            json = serviceKO("VisualiseAccount Failed : couldn't retrieve the informations");
        }
        return json;
    }

    /* service pour changer un des champs du compte utilisateur */
    public static JSONObject changeFieldAccount(String login, String pwd, String field_name, String new_value,String token){
        JSONObject json;

        if(!userConnected(login)) return serviceKO("ChangeFieldAccount Fail : User not connected");

        if(!SessionTools.checkToken(token, login)){
            return serviceKO("ChangeFieldAccount Fail : Wrong token");
        }
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
            json = serviceKO("AccountModification Failed : couldn't change your account's information");
        }

        return json;
    }
}
