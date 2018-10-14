package servlet;

import services.UserService;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.ValidationException;

import static services.ServiceTools.serviceKO;

@WebServlet(
        name = "SubscribeServlet",
        urlPatterns = {"/subscribe"}
)
public class SubscribeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType( "text / plain" );
        PrintWriter out = resp.getWriter();


        //Login*Mdp*ConfirmMdp*Email*Nom*Prenom*DateNaissance(dd/mm/aaaa)*Pays->void

        try {
            String login = ValidatorHelper.getParam(req, "login", true);
            String mdp = ValidatorHelper.getParam(req, "password", true);
            String cmdp = ValidatorHelper.getParam(req, "confirmPassword", true);
            String email = ValidatorHelper.getParam(req, "email", true);
            String nom = ValidatorHelper.getParam(req, "lastName", true);
            String prenom = ValidatorHelper.getParam(req, "firstName", true);
            String dateNaiss = ValidatorHelper.getParam(req, "dateNaiss", true);
            String pays = ValidatorHelper.getParam(req, "country", true);

            if(ValidatorHelper.isEmail(email) && ValidatorHelper.isDateSQL(dateNaiss) && !ValidatorHelper.containsWhiteSpace(login)
                    && !ValidatorHelper.containsWhiteSpace(mdp) && !ValidatorHelper.containsWhiteSpace(nom) && !ValidatorHelper.containsWhiteSpace(prenom)
            ) {
                out.print(
                        UserService.subscribe(login, mdp, cmdp,email, nom, prenom, Date.valueOf(dateNaiss), pays)
                );
            }
            else {
                out.print(serviceKO("Subscribe fail error param"));
            }
        }catch (ValidationException ve){
            out.print(serviceKO(ve.getMessage()));
        }
        out.close();
    }

}
