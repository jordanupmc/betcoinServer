package services;

import bd.UserTools;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.sql.SQLException;

import static bd.UserTools.checkPasswd;
import static bd.UserTools.userConnected;
import static services.ServiceTools.serviceKO;
import static services.ServiceTools.serviceOK;

public class LoginService {
    public static JSONObject connect(String login, String mdp){

        if((login == null) || (mdp == null)){
            return serviceKO("Connect Fail : Wrong arguments, expecting: login password");
        }

        boolean passwd_OK = false;
        try {
            passwd_OK = checkPasswd(login, mdp);
        } catch (URISyntaxException e) {
            return serviceKO("Connect Fail : URISyntaxException");
        } catch (SQLException e) {
            return serviceKO("Connect Fail : SQLException");
        }
        if(!passwd_OK) return serviceKO("Connect Fail : Invalid login and/or wrong password");

        boolean connected = userConnected(login);
        if(connected) return serviceKO("Connect Fail : User already connected");

        String token = UserTools.connect(login, mdp);

        JSONObject j = serviceOK();
        j.put("token", token);
        return j;
    }
}