package omgcheesecake.outbreak;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPref extends MapModel{

    private static SharedPreferences sharedPreferences;
    private static String option;

    public SharedPref(Activity activity){
        sharedPreferences = activity.getSharedPreferences("outbreakReportOption", Context.MODE_PRIVATE);
        if(this.option == null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("option", "1 Year");
            editor.apply();
        }
    }

    public String getOption(){
        option = sharedPreferences.getString("option", null);
        return option;
    }

    public void setOption(String s){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("option", s);
        editor.apply();
    }
}
