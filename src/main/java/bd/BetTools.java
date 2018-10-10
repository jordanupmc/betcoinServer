package bd;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BetTools {

    public static JSONArray getListPoolsActive(){
        JSONArray ar=new JSONArray();
        String query =
                "SELECT idbetpool, name, openingbet, closingbet, resultbet, cryptocurrency, pooltype FROM BetPool WHERE closingBet > NOW()";
        try(Connection c = Database.getConnection();
            PreparedStatement pstmt = c.prepareStatement(query);
            ResultSet v = pstmt.executeQuery();
        ) {
            JSONObject j=null;

            while(v.next()){
                j =new JSONObject();
                j.put("idbetpool", v.getInt(1));
                j.put("name", v.getString(2));
                j.put("openingbet", v.getTimestamp(3));
                j.put("closingbet", v.getTimestamp(4));
                j.put("resultbet", v.getBigDecimal(5));
                j.put("cryptocurrency", v.getString(6));
                j.put("pooltype", v.getBoolean(7));
                ar.put(j);
            }
        }catch(Exception e){
        }
        finally {
            return ar;
        }
    }
}
