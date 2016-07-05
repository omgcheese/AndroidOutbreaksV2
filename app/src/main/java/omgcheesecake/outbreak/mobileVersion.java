package omgcheesecake.outbreak;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class mobileVersion{
    private SharedPreferences sharedPreferences;

    public mobileVersion(Activity activity){
        sharedPreferences = activity.getSharedPreferences("MobileVersion", Context.MODE_PRIVATE);
    }

    public int getVersion(){
        int version = sharedPreferences.getInt("version", 0);
        return version;
    }

    public void setVersion(int newVer){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("version", newVer);
        editor.apply();
    }
}
