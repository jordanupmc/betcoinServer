package services;

import bd.SessionTools;
import bd.UserTools;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;

import static bd.SessionTools.userConnected;
import static bd.UserTools.checkPasswd;
import static services.ServiceTools.serviceKO;
import static services.ServiceTools.serviceOK;

public class AccountService {

    /* service pour la visualisation des informations d'un compte utilisateur*/
    public static JSONObject visualiseAcc(String login, String token){
        JSONObject json;

        boolean connected = userConnected(login);
        if(!connected) return serviceKO("You are not connected", true);

        if(!SessionTools.checkToken(token, login)){
            return serviceKO("Please, login once again", true);
        }
        try {
            json = UserTools.visualiseAccount(login);
        } catch (URISyntaxException e) {
            json = serviceKO("VisualiseAccount Failed", false);
        } catch (SQLException e) {
            json = serviceKO("VisualiseAccount Failed", false);
        }
        if(json==null){
            json = serviceKO("We couldn't retrieve informations about your account", false);
        }
        return json;
    }

    /* service pour changer un des champs du compte utilisateur */
    public static JSONObject changeFieldAccount(String login, String pwd, ArrayList<String> field_name,
                                                ArrayList<String> new_value, String token){
        JSONObject json;

        if(!userConnected(login)) return serviceKO("You are not connected", true);

        if(!SessionTools.checkToken(token, login)){
            return serviceKO("Please, login once again", true);
        }
        try {
            if (!checkPasswd(login, pwd)) {
                return serviceKO("Wrong password", false);
            }
            if(UserTools.accountModification(login,field_name,new_value)){
                json = serviceOK();
            }else{
                json = serviceKO("We couldn't retrieve informations about your account", false);
            }
        }catch(SQLException e){
            return serviceKO("AccountModification Failed", false);
        }catch (URISyntaxException e){
            return serviceKO("AccountModification Failed", false);
        }

        return json;
    }
}
