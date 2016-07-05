package omgcheesecake.outbreak;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SqlLiteModel{

    private static SqlLiteController sqlLiteController;
    private Context context;
    private static HashMap<String, String> LastRow;

    SqlLiteModel(Context context){
        this.context = context;
    }

    public void initiateController(){
        if(this.sqlLiteController == null){
            this.sqlLiteController = new SqlLiteController(this.context);
            this.sqlLiteController.onCreate();
        }
    }

    public ArrayList<HashMap<String, String>> virusByTime(String date){
        return this.sqlLiteController.virusByTime(date);
    }

    public void SQLDestroy(){
        this.sqlLiteController.destroySQL(context);
    }

    public ArrayList<HashMap<String, String>> getVirusesfromCountry(String country, String date){
        return this.sqlLiteController.getVirusesfromCountry(country, date);
    }

    public void LoadingData(JSONObject jsonObject){
        try {
            //Check if it's same as lastrow we have extracted
            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put("virusname",jsonObject.getString("virusname"));
            hashMap.put("country", jsonObject.getString("country"));
            hashMap.put("lastupdated", jsonObject.getString("lastupdated"));
            String date = sqlLiteController.YYYYMMDDtoString(Integer.valueOf(hashMap.get("lastupdated")));

            if(LastRow != null){
                if(!LastRow.get("lastupdated").equals(date)) {
                    if(!LastRow.get("country").equals(hashMap.get("country"))){
                        if(!LastRow.get("virusname").equals(hashMap.get("virusname"))){
                            //put these data into SQLite database
                            sqlLiteController.insertRegionDatabase(jsonObject.getString("virusname"), jsonObject.getString("country"), jsonObject.getInt("lastupdated"));
                        }
                    }
                }
            }
            else {
                //put these data into SQLite database
                sqlLiteController.insertRegionDatabase(jsonObject.getString("virusname"), jsonObject.getString("country"), jsonObject.getInt("lastupdated"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void closeCursor(){
        this.sqlLiteController.closeCuror();
    }

//    No longer Supoorted
//    public Boolean LastRowComparison(ArrayList<HashMap<String, String>> lastrow_1){
//
//        ArrayList<HashMap<String, String>> lastrow_2 = sqlLiteController.retrieveRegionDatabaseLastRow(1, 0);
//        if(LastRow == null && lastrow_2.size() > 0){
//            LastRow = lastrow_2.get(0);
//        }
//        Boolean comparison = sqlLiteController.lastrowsCompare(lastrow_2, lastrow_1);
//        if (!comparison) {
//            //if they are not same, start downloading full content from the server
//            return true;
//        }
//        //if the last row is same, DO NOT download
//        return false;
//    }
}
