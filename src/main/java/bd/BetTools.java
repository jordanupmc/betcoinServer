package bd;

import com.mongodb.client.MongoCollection;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static bd.Database.getMongoCollection;

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

    /* Renvoi true si un pari est toujours annulable */
    public static boolean canCancelBet(String idPool) throws URISyntaxException, SQLException {

        Connection c = Database.getConnection();
        String query = "SELECT * FROM BETPOOL WHERE idbetpool='" + idPool
                + "'";
        PreparedStatement pstmt = c.prepareStatement(query);

        ResultSet res = pstmt.executeQuery();
        if (res.next()) {
            if(res.getTimestamp("closingbet").after(new Timestamp(System.currentTimeMillis()))){
                pstmt.close();
                c.close();
                return true;
            }
        }
        pstmt.close();
        c.close();
        return false;
    }

    public static boolean cancelBet(String login, String idPool){
        MongoCollection<Document> collection = getMongoCollection("L_Bet");
        Document d =
                collection
                        .find(new BsonDocument().append("idBetPool", new BsonString(idPool)))
                        .first();
        if (d == null) {
            return false;
        } else {
            BsonDocument filter = new BsonDocument().append("idBetPool", new BsonString(idPool));

            List<Document> bets = (List<Document>) d.get("bet");

            for (int i = 0; i < bets.size(); i++) {
                if (bets.get(i).get("gamblerLogin").equals(login)) {
                    bets.remove(i);
                    collection.updateOne(filter, new Document("$set", new Document("bet", bets)));
                    return true;
                }
            }

            return false;
        }
    }
}
