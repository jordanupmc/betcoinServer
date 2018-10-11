package services;

import bd.SessionTools;
import bd.UserTools;
import org.json.JSONObject;

import static bd.UserTools.checkPasswd;
import static bd.UserTools.userConnected;
import static tools.ServiceTools.serviceKO;
import static tools.ServiceTools.serviceOK;

public class EnterPoolService {
    public static JSONObject enterPool(String login, String idPool, String token){

        if((login == null) || (idPool == null) || (token == null)) {
            return serviceKO("EnterPool Fail : Wrong arguments, expecting: login idPool token");
        }

        boolean connected = userConnected(login);
        if(!connected) return serviceKO("EnterPool Fail : User not connected");

        if(!SessionTools.checkToken(token, login)){
            return serviceKO("EnterPool Fail : Wrong token");
        }

        UserTools.enterPool(login, idPool);
        return serviceOK();
    }
}