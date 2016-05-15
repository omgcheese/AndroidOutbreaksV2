package omgcheesecake.outbreak;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ApiModel{

    private ApiController apiController;
    private static ApiModelListener apiModelListener;

    public ApiModel(){
        if(this.apiController == null){
            this.apiController = new ApiController();
        }
        this.apiController.setApiCall(new ApiController.ApiListener() {
            @Override
            public void onLastRow(ArrayList<HashMap<String, String>> lastrow) {
                if(apiModelListener != null){
                    apiModelListener.apimodelonLastRow(lastrow);
                }
            }

            @Override
            public void onDataLoaded(JSONObject jsonObject) {
                apiModelListener.apimodelonDataLoaded(jsonObject);
            }

            @Override
            public void onComplete() {
                apiModelListener.apimodelComplete();
            }
        });
    }

    public void GetLastRow(){
        apiController.GetLastRow();
    }

    public interface ApiModelListener{
        void apimodelonLastRow(ArrayList<HashMap<String, String>> lastrow);
        void apimodelonDataLoaded(JSONObject jsonObject);
        void apimodelComplete();
    }

    public void setApimodelListener(ApiModelListener apiModelListener){
        this.apiModelListener = apiModelListener;
    }

    public void downloadContent(){
        this.apiController.downloadContent();
    }
}
