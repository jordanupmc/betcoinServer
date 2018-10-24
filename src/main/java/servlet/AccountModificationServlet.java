package servlet;


import com.mongodb.BasicDBList;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import services.AccountService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static services.ServiceTools.serviceKO;

@WebServlet(
        name = "AccountModificationServlet",
        urlPatterns = {"/changeAccountInfo"}
)
public class AccountModificationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text / plain");
        PrintWriter out = resp.getWriter();
        JSONObject j = ValidatorHelper.getJSONParameter(req,resp);

        try {
            String login = ValidatorHelper.getParam(j, "login", true);
            String pwd = ValidatorHelper.getParam(j, "password", true);
            String field_name = ValidatorHelper.getParam(j, "fieldName", true);
            String new_value = ValidatorHelper.getParam(j, "newValue", true);
            String token = ValidatorHelper.getParam(j, "token", true);

            BasicDBList arrayfield = (BasicDBList) JSON.parse(field_name);
            BasicDBList arrayValue = (BasicDBList) JSON.parse(new_value);
            ArrayList<String> fieldTab = new ArrayList<>();
            ArrayList<String> valueTab = new ArrayList<>();
            for (Object o : arrayfield) {
                String tmp = o.toString();
                if(tmp.contains("iduser")){
                    out.print(serviceKO("Account Modification Failed : no change in idUser allowed"));
                    out.close();
                    return;
                }else if(tmp.contains("login")){
                    out.print(serviceKO("Account Modification Failed : no change in login allowed"));
                    out.close();
                    return;
                }
                if(!arrayValue.get(arrayfield.indexOf(o)).toString().equals("")){
                    fieldTab.add(tmp);
                }

            }
            for (Object o : arrayValue) {
                if(!o.toString().equals("")) {
                    valueTab.add(o.toString());
                }
            }
            JSONObject json = new JSONObject();

            if ((login != null) && (token != null) && (pwd != null) && (field_name != null) && (new_value != null)) {
                json = AccountService.changeFieldAccount(login, pwd, fieldTab, valueTab, token);
            } else {
                json = serviceKO("Missing arguments");
            }
            out.print(json);

        }catch(Exception e){
            out.print(serviceKO(e.getMessage()));
        }
        out.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {





    }
}
