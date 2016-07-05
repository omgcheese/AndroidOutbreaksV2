package omgcheesecake.outbreak;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class LoadingScrn extends AppCompatActivity {
    private ApiModel apiModel;
    private SqlLiteModel sqlLiteModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = new Intent(this, MapModel.class);

        sqlLiteModel = new SqlLiteModel(this);
        sqlLiteModel.initiateController();

        //initiate API connection?
        //getting last row is not recommended anymore

        final mobileVersion version = new mobileVersion(this);
        int versionNum = version.getVersion();
        if(versionNum == 0){
            //if versionNum is zero, delete SQLDatabase because you will be downloading everything
            sqlLiteModel.SQLDestroy();
            sqlLiteModel.initiateController();
        }
        apiModel = new ApiModel(version.getVersion());
        apiModel.setApimodelListener(new ApiModel.ApiModelListener() {
            @Override
            public void apimodelonDataLoaded(JSONObject jsonObject) {
                    sqlLiteModel.LoadingData(jsonObject);
            }

            @Override
            public void apimodelComplete(int currentVer) {
                version.setVersion(currentVer);
                startActivity(intent);
                finish();
            }

            @Override
            public void apimodelVersion(boolean same, int difference, int currentVer) {
                if(same){
                    //if it's same, we do not need to care for extra download
                    startActivity(intent);
                    finish();
                }
                else{
                    //if version is different, we have to download one more time
                    downloadContent(difference, currentVer);
                }
            }
        });

        apiModel.getVersion();
    }

    public void downloadContent(int difference, int currentVer){
        apiModel.downloadContent(difference, currentVer);
    }
}
