package services;

import bd.PoolTools;
import bd.SessionTools;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.sql.SQLException;

import static bd.UserTools.userConnected;
import static services.ServiceTools.serviceKO;
import static services.ServiceTools.serviceOK;

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

        try {
            if(!PoolTools.poolExist(idPool)){
                return serviceKO("EnterPool Fail : This pool does not exist");
            }
        } catch (URISyntaxException e) {
            return serviceKO("EnterPool Fail : URISyntaxException");
        } catch (SQLException e) {
            e.printStackTrace();
            return serviceKO("EnterPool Fail : SQLException");
        }

        PoolTools.enterPool(login, idPool);
        return serviceOK();
    }
}