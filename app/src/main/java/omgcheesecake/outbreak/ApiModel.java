package omgcheesecake.outbreak;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ApiModel{

    private ApiController apiController;
    private static ApiModelListener apiModelListener;

    public ApiModel(int version){
        if(this.apiController == null){
            this.apiController = new ApiController(version);
        }
        this.apiController.setApiCall(new ApiController.ApiListener() {

//            No longer Support onLastRow method anymore
//            @Override
//            public void onLastRow(ArrayList<HashMap<String, String>> lastrow) {
//                if(apiModelListener != null){
//                    apiModelListener.apimodelonLastRow(lastrow);
//                }
//            }

            @Override
            public void onDataLoaded(JSONObject jsonObject) {
                apiModelListener.apimodelonDataLoaded(jsonObject);
            }

            @Override
            public void onComplete(int currentVer) {
                apiModelListener.apimodelComplete(currentVer);
            }

            @Override
            public void versionCompare(boolean same, int difference, int currentVer) {
                apiModelListener.apimodelVersion(same, difference, currentVer);
            }
        });
    }

//    No longer support Last Row functions
//    public void GetLastRow(){
//        apiController.GetLastRow();
//    }

    public interface ApiModelListener{
        void apimodelonDataLoaded(JSONObject jsonObject);
        void apimodelComplete(int currentVer);
        void apimodelVersion(boolean same, int difference, int currentVer);
    }

    public void setApimodelListener(ApiModelListener apiModelListener){
        this.apiModelListener = apiModelListener;
    }

    public void downloadContent(int difference, int currentVer){
        this.apiController.downloadContent(difference, currentVer);
    }

    public void getVersion(){
        apiController.getVersion();
    }
}
