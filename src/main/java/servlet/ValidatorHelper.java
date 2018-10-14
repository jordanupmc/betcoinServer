package servlet;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.ValidationException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorHelper {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private ValidatorHelper(){}

    /*Return null si le champs est null ou vide ET qu'il n'est pas requis sinon return le field*/
    public static String getParam(HttpServletRequest req, String paramName, boolean required) throws ValidationException {
        String field = req.getParameter(paramName);
        if(field == null || field.trim().isEmpty()){
            if(required)
                throw new ValidationException(paramName+ " is required");
            return null;
        }
        return field;
    }

    /*Return si la chaine est un email*/
    public static boolean isEmail(String s) throws ValidationException{
        if(s == null) return false;
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(s);
        if(!matcher.find())
            throw new ValidationException(s+ " is not a valid email");
        return true;
    }
    public static boolean containsWhiteSpace(String s) throws ValidationException{
        Pattern whitespace = Pattern.compile("\\s+");
        Matcher matcher = whitespace.matcher(s);
        String result = "";
        if (matcher.find()) {
            throw new ValidationException(s+ " contains white space");
        }
        return false;
    }

    public static boolean isDateSQL(String s) throws ValidationException{
        format.setLenient(false);
        if(s == null) return false;

        try{
            format.parse(s);
            return true;
        } catch (ParseException e) {
            throw new ValidationException(s+ " is not a valid date ( yyyy-MM-dd )");
        }
    }
}
