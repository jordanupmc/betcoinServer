package services;

import bd.UserTools;
import org.json.JSONObject;

import static bd.UserTools.checkPasswd;
import static bd.UserTools.userConnected;
import static tools.ServiceTools.serviceKO;
import static tools.ServiceTools.serviceOK;

public class EnterPoolService {
    public static JSONObject enterPool(String login, String idPool){

        if((login == null) || (idPool == null)) {
            return serviceKO("EnterPool Fail : Wrong arguments, expecting: login idPool");
        }

        boolean connected = userConnected(login);
        if(!connected) return serviceKO("EnterPool Fail : User not connected");

        UserTools.enterPool(login, idPool);
        return serviceOK();
    }
}