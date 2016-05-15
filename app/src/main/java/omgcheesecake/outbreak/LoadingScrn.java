package omgcheesecake.outbreak;

import android.content.Intent;
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
        apiModel = new ApiModel();
        apiModel.GetLastRow();
        apiModel.setApimodelListener(new ApiModel.ApiModelListener() {
            @Override
            public void apimodelonLastRow(ArrayList<HashMap<String, String>> lastrow) {
                if(sqlLiteModel.LastRowComparison(lastrow)){
                    apiModel.downloadContent();
                }
                else {
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void apimodelonDataLoaded(JSONObject jsonObject) {
                    sqlLiteModel.LoadingData(jsonObject);
            }

            @Override
            public void apimodelComplete() {
                startActivity(intent);
                finish();
            }
        });
    }
}
