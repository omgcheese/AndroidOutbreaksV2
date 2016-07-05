package omgcheesecake.outbreak;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ApiController{

    private static ApiListener apiListener;
    private int version;

    public ApiController(int version){
        this.apiListener = null;
        this.version = version;
    }

//    This function is no longer supported
//    public void GetLastRow(){
//        lastrow task = new lastrow();
//        task.execute("http://afternoon-garden-52459.herokuapp.com/api/get/lastrow");
//    }

    public void getVersion(){
        //If there is data version, send it via query string
        GetVersion getVersion = new GetVersion();
        getVersion.execute("http://afternoon-garden-52459.herokuapp.com/api/get/version?version="+this.version);

    }

    public void setApiCall(ApiListener apiListener){
        this.apiListener = apiListener;
    }

    public void downloadContent(int diff, int currversion){
        //Do the downloadtask in here
        DownloadTask task = new DownloadTask(currversion);
        task.execute("http://afternoon-garden-52459.herokuapp.com/api/get/all?limit="+ diff);
    }

    public interface ApiListener{
        //void onLastRow(ArrayList<HashMap<String, String>> lastrow);
        void onDataLoaded(JSONObject jsonObject);
        void onComplete(int currentVer);
        void versionCompare(boolean same, int difference, int currentVer);
    }

    public class GetVersion extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection httpURLConnection = null;

            try {
                url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while(data > -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            int difference = 0;
            int currentVer = 0;
            boolean same = false;
            try {
                JSONObject jsonObject = new JSONObject(result);
                //if you get correct result, you will get a format like this:
                /*
                {
                    date: moment().format("YYYYMMDD"),
                    succuess: true,
                    same: false,
                    diff: sumofDiff <- Integer
                }
                */
                if(jsonObject.getBoolean("same")){
                    same = true;
                }
                else{
                    same = false;
                    difference = jsonObject.getInt("diff");
                    currentVer = jsonObject.getInt("currentVersion");
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }

            apiListener.versionCompare(same, difference, currentVer);

        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String>{
        private int currentVer;

        public DownloadTask(int currentVer){
            this.currentVer = currentVer;
        }

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);


                int data = reader.read();
                while (data != -1) {

                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);

                String virusdata = jsonObject.getString("list");

                JSONArray data = new JSONArray(virusdata);

                for (int i = 0; i< data.length(); i++){
                    JSONObject jsonpart = data.getJSONObject(i);
                    apiListener.onDataLoaded(jsonpart);
                }

                apiListener.onComplete(this.currentVer);



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
