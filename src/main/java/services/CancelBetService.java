package services;

import bd.BetTools;
import bd.SessionTools;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.sql.SQLException;

import static bd.BetTools.betPoolOpen;
import static bd.UserTools.userConnected;
import static services.ServiceTools.serviceKO;
import static services.ServiceTools.serviceOK;

public class CancelBetService {

    /* service d'annulation d'un pari */
    public static JSONObject cancelBet(String login, String idPool, String token){

        if((login == null) || (idPool == null) || (token == null)) {
            return serviceKO("CancelBet Fail : Wrong arguments, expecting: login idPool token");
        }

        boolean connected = userConnected(login);
        if(!connected) return serviceKO("CancelBet Fail : User not connected");

        if(!SessionTools.checkToken(token, login)){
            return serviceKO("CancelBet Fail : Wrong token");
        }

        try {
            boolean canCancel = betPoolOpen(idPool);
            if(!canCancel){
                return serviceKO("CancelBet Fail : Too late to cancel");
            }
        } catch (URISyntaxException e) {
            return serviceKO("CancelBet Fail : URISyntaxException");
        } catch (SQLException e) {
            e.printStackTrace();
            return serviceKO("CancelBet Fail : SQLException");
        }

        try {
            if(BetTools.cancelBet(login, idPool)){
                return serviceOK();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return serviceKO("CancelBet Fail : URISyntaxException");
        } catch (SQLException e) {
            e.printStackTrace();
            return serviceKO("CancelBet Fail : SQLException");

        }

        return serviceKO("CancelBet Fail : No such Pool or No bet done");
    }
}