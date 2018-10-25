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
        if(!connected) return serviceKO("VisualiseAccount Fail : User not connected");

        if(!SessionTools.checkToken(token, login)){
            return serviceKO("VisualiseAccount Fail : Wrong token");
        }
        try {
            json = UserTools.visualiseAccount(login);
        } catch (URISyntaxException e) {
            json = serviceKO(e.getMessage());
        } catch (SQLException e) {
            json = serviceKO(e.getMessage());
        }
        if(json==null){
            json = serviceKO("VisualiseAccount Failed : couldn't retrieve the informations");
        }
        return json;
    }

    /* service pour changer un des champs du compte utilisateur */
    public static JSONObject changeFieldAccount(String login, String pwd, ArrayList<String> field_name,
                                                ArrayList<String> new_value, String token){
        JSONObject json;

        if(!userConnected(login)) return serviceKO("ChangeFieldAccount Fail : User not connected");

        if(!SessionTools.checkToken(token, login)){
            return serviceKO("ChangeFieldAccount Fail : Invalid token");
        }
        try {
            if (!checkPasswd(login, pwd)) {
                return serviceKO("AccountModification Failed : Wrong password");
            }
        }catch(SQLException e){
            return serviceKO("AccountModification Failed : SQLException ");
        }catch (URISyntaxException e){
            return serviceKO("AccountModification Failed : URISyntaxException ");
        }
        try {
            if(UserTools.accountModification(login,field_name,new_value)){
                json = serviceOK();
            }else{
                json = serviceKO("AccountModification Failed : couldn't change your account's information");
            }
        } catch (URISyntaxException e) {
            json = serviceKO(e.getMessage());
        } catch (SQLException e) {
            json = serviceKO(e.getMessage());
        }

        return json;
    }
}
