package servlet;

import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorHelper {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private ValidatorHelper() {
    }

    /*Return null si le champs est null ou vide ET qu'il n'est pas requis sinon return le field*/
    public static String getParam(HttpServletRequest req, String paramName, boolean required) throws ValidationException {
        String field = req.getParameter(paramName);
        if (field == null || field.trim().isEmpty()) {
            if (required)
                throw new ValidationException(paramName + " is required");
            return null;
        }
        return field;
    }


    public static String getParam(JSONObject jo, String paramName, boolean required) throws ValidationException {
        if (jo == null) throw new ValidationException("JSON param is empty");
        if(jo.has(paramName)) {
            String field = jo.getString(paramName);
            if (field == null || field.trim().isEmpty()) {
                if (required)
                    throw new ValidationException(paramName + " is required");
                return null;
            }
            return field;
        }
        if (required)
            throw new ValidationException(paramName + " is required");
        return null;
    }

    /*Return si la chaine est un email*/
    public static boolean isEmail(String s) throws ValidationException {
        if (s == null) return false;
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(s);
        if (!matcher.find())
            throw new ValidationException(s + " is not a valid email");
        return true;
    }

    /*Return si s contient des espaces*/
    public static boolean containsWhiteSpace(String s) throws ValidationException {
        Pattern whitespace = Pattern.compile("\\s+");
        Matcher matcher = whitespace.matcher(s);
        String result = "";
        if (matcher.find()) {
            throw new ValidationException(s + " contains white space");
        }
        return false;
    }


    /*Return si s est correspond au format d'une date SQL*/
    public static boolean isDateSQL(String s) throws ValidationException {
        format.setLenient(false);
        if (s == null) return false;

        try {
            format.parse(s);
            return true;
        } catch (ParseException e) {
            throw new ValidationException(s + " is not a valid date ( yyyy-MM-dd )");
        }
    }

    /*Return si date < today*/
    public static boolean isBeforeToday(String date) throws ValidationException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d = null;
        try {
            d = sdf.parse(date);
            Date currentDate = new Date();
            if (sdf.format(d).compareTo(sdf.format(currentDate)) == 0 || d.after(currentDate))
                throw new ValidationException("Vous ne pouvez pas parrié à votre age");
            return d.before(currentDate);
        } catch (ParseException e) {
            throw new ValidationException("Vous ne pouvez pas parrié à votre age");
        }

    }

    public static boolean checkBoolean(String val) throws ValidationException {
        if (val == null) throw new ValidationException(val + " is not a boolean value");

        val = val.toLowerCase();
        if (val.equals("true") || val.equals("false"))
            return true;
        throw new ValidationException(val + " is not a boolean value");
    }

    /*Return un JSONObject a partir d'un HttpServletRequest*/
    public static JSONObject getJSONParameter(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = req.getReader().readLine()) != null) {
            sb.append(s);
        }
        PrintWriter out = resp.getWriter();
        if (sb.length() <= 1) return null;
        return new JSONObject(sb.toString());
    }

    public static boolean isInteger(String s) {
        return isInteger(s, 10);
    }

    public static boolean isInteger(String s, int radix) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) return false;
                else continue;
            }
            if (Character.digit(s.charAt(i), radix) < 0) return false;
        }
        return true;
    }

    public static boolean isLengthInfTo(String s, int limit) throws ValidationException {
        if(s.length()<limit)return true;
        throw  new ValidationException(s.substring(0,11) + "... dépasse la limite de charactere (" + limit + ")");
    }
}