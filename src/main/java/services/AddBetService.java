package services;

import bd.BetTools;
import bd.SessionTools;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.sql.SQLException;

import static bd.BetTools.canCancelBet;
import static bd.UserTools.userConnected;
import static services.ServiceTools.*;

public class AddBetService {
    public static JSONObject addBet(String token, String login, String idPool, String ammount, String value) {
        if ((login == null) || (idPool == null) || (ammount == null) || (value == null) || (token == null)) {
            return serviceKO("AddBet Fail : Wrong arguments, expecting: login idPool ammount value");
        }

        boolean connected = userConnected(login);
        if (!connected) return serviceKO("AddBet Fail : User not connected");

        if (!SessionTools.checkToken(token, login)) {
            return serviceKO("AddBet Fail : Wrong token");
        }

        try {
            if (canCancelBet(idPool)) {
                return serviceKO("AddBet Fail : Too late to add a new bet");
            }

        } catch (SQLException e) {
            return serviceKO("AddBet Fail : SQLException");

        } catch (URISyntaxException e) {
            return serviceKO("AddBet Fail : URISyntaxException");

        }
        if (BetTools.addBet(login, idPool, Integer.parseInt(ammount), Double.parseDouble(value)))
            return serviceOK();

        return serviceKO("AddBet Fail : Unkwown Error");
    }
}
