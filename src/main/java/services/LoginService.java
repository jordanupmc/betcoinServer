package services;

import bd.SessionTools;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.sql.SQLException;

import static bd.SessionTools.userConnected;
import static bd.UserTools.accountClosed;
import static bd.UserTools.checkPasswd;
import static services.ServiceTools.serviceKO;
import static services.ServiceTools.serviceOK;

public class LoginService {

    /* service permettant la connexion d'un utilisateur */
    public static JSONObject connect(String login, String mdp){

        boolean accountClosed = false;
        boolean passwd_OK = false;
        try {
            passwd_OK = checkPasswd(login, mdp);
            accountClosed= accountClosed(login);
        } catch (URISyntaxException e) {
            return serviceKO("Connect Fail");
        } catch (SQLException e) {
            return serviceKO("Connect Fail");
        }

        if(accountClosed) return serviceKO("Account closed");
        if(!passwd_OK) return serviceKO("Invalid login and/or wrong password");

        // Si l'on se connecte alors que l'on est deja connecté, la nouvelle connexion est prioritaire
       /* boolean connected = userConnected(login);
        if(connected) return serviceKO("Connect Fail : User already connected");*/

        String token = null;
        try {
            token = SessionTools.connect(login, mdp);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return serviceKO("Connect Fail");
        } catch (SQLException e) {
            e.printStackTrace();
            return serviceKO("Connect Fail");
        }

        JSONObject j = serviceOK();
        j.put("token", token);
        return j;
    }

    /* service de deconnexion d'un utilisateur */
    public static JSONObject disconnect(String login, String token){
        JSONObject json;

        if(!userConnected(login)) return serviceKO("You are not connected", true);

        if(SessionTools.checkToken(token, login)){
            SessionTools.disconnect(login,token);
            json = serviceOK();
            json.put("disconnectedLogin",login);
        }else{
            json = serviceKO("Please, log again", true);
        }
        return json;
    }
}