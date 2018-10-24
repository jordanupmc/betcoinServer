package servlet;


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
            String field_name = req.getParameter("fieldName");
            String new_value = req.getParameter("newValue");
            String token = ValidatorHelper.getParam(j, "token", true);

            JSONObject tab = new JSONObject();
            tab.append("field",field_name);
            tab.append("value",new_value);
            JSONArray fieldList = (JSONArray) tab.get("field");
            JSONArray valueList = (JSONArray) tab.get("value");
            ArrayList<String> fieldTab= new ArrayList<>();
            ArrayList<String> valueTab = new ArrayList<>();

            for (int i = 0 ; i < fieldList.length();i++) {
                String tmp = fieldList.get(1).toString();
                if(tmp.contains("iduser")){
                    out.print(serviceKO("Account Modification Failed : no change in idUser allowed"));
                    out.close();
                    return;
                }else if(tmp.contains("login")){
                    out.print(serviceKO("Account Modification Failed : no change in login allowed"));
                    out.close();
                    return;
                }
                fieldTab.add(tmp);
            }
            for (int i = 0 ; i < valueList.length();i++) {
                valueTab.add(valueList.get(i).toString());
            }
            JSONObject json = new JSONObject();

            if ((login != null) && (token != null) && (pwd != null) && (field_name != null) && (new_value != null)) {
                json = AccountService.changeFieldAccount(login, pwd, fieldTab, valueTab, token);
            } else {
                json = serviceKO("Missing login");
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
