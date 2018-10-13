package bd;

import com.mongodb.client.MongoCollection;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    public static boolean quitPool(String login, String idPool){
        MongoCollection<Document> collection = getMongoCollection("SubscribePool");
        Document d =
                collection
                        .find(new BsonDocument().append("gamblerLogin", new BsonString(login)))
                        .first();
        if(d==null){
            JOptionPane.showMessageDialog(null,"User has no subscription yet");
            return false;
        }else{
            BsonDocument filter = new BsonDocument().append("gamblerLogin", new BsonString(login));
            List<Document> pools = (List<Document>) d.get("idBetPool");
            for(int i = 0; i < pools.size();i++){
                if(pools.get(i).get("idPool").equals(idPool)){
                    pools.remove(i);
                    collection.updateOne(filter, new Document("$set", new Document("idBetPool", pools)));
                    return cancelBet(login, idPool);
                }
            }
            return false;
        }

    }
}
