package services;

import bd.BetTools;
import bd.UserTools;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.sql.SQLException;

import static bd.BetTools.canCancelBet;
import static bd.UserTools.userConnected;
import static tools.ServiceTools.serviceKO;
import static tools.ServiceTools.serviceOK;

public class CancelBetService {
    public static JSONObject cancelBet(String login, String idPool){

        if((login == null) || (idPool == null)) {
            return serviceKO("CancelBet Fail : Wrong arguments, expecting: login idPool");
        }

        boolean connected = userConnected(login);
        if(!connected) return serviceKO("CancelBet Fail : User not connected");

        try {
            boolean canCancel = canCancelBet(idPool);
            if(!canCancel){
                return serviceKO("CancelBet Fail : Too late to cancel");
            }
        } catch (URISyntaxException e) {
            return serviceKO("CancelBet Fail : URISyntaxException");
        } catch (SQLException e) {
            return serviceKO("CancelBet Fail : SQLException");
        }

        if(BetTools.cancelBet(login, idPool)){
            return serviceOK();
        }

        return serviceKO("CancelBet Fail : No such Pool or No bet done");
    }
}