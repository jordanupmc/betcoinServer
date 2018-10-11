package services;

import bd.BetTools;
import bd.PoolTools;
import bd.SessionTools;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.sql.SQLException;

import static bd.BetTools.canCancelBet;
import static bd.UserTools.userConnected;
import static tools.ServiceTools.serviceKO;
import static tools.ServiceTools.serviceOK;

public class MessagePoolService {
    public static JSONObject messagePool(String login, String idPool, String token, String message){

        if((login == null) || (idPool == null) || (token == null) || (message == null)) {
            return serviceKO("MessagePool Fail : Wrong arguments, expecting: login idPool message");
        }

        boolean connected = userConnected(login);
        if(!connected) return serviceKO("MessagePool Fail : User not connected");

        if(!SessionTools.checkToken(token, login)){
            return serviceKO("MessagePool Fail : Wrong token");
        }

        try {
            if(!PoolTools.poolExist(idPool)){
                return serviceKO("MessagePool Fail : This pool does not exist");
            }
        } catch (URISyntaxException e) {
            return serviceKO("MessagePool Fail : URISyntaxException");
        } catch (SQLException e) {
            e.printStackTrace();
            return serviceKO("MessagePool Fail : SQLException");

        }

        PoolTools.messagePool(login, idPool, message);
        return serviceOK();
    }
}
