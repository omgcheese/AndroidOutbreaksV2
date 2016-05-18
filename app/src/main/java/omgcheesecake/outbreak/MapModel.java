package omgcheesecake.outbreak;

import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Main Model
//This model should be shared with other models as starting
//However other models should not all connected; Use implements

public class MapModel extends AppCompatActivity {

    private static TextView reportTitle;
    private SqlLiteModel sqlLiteModel;
    private FragmentManager fm;
    private SharedPref sharedPref;
    private RecycleList recycleList;
    private MapController mapController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_model);

        //Init Sharedpref
        sharedPref = new SharedPref(this);

        //Init Google Map
        GoogleMapInit();

        //Init SQL Model
        SQLInit();

        //Init report Title
        OutbreakInit();

    }

    //Need to hide the report title until everything is loaded
    public void OutbreakInit() {
        if (this.reportTitle == null || this.fm == null) {
            this.reportTitle = (TextView) findViewById(R.id.BottomNoti);
            this.fm = getSupportFragmentManager();
        }
        reportTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ReportModel reportModel = ReportModel.newInstance();
                reportModel.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragmentTheme);
                reportModel.show(fm, "outbreak_report");
                return false;
            }
        });
    }

    public void SQLInit() {
        if (this.sqlLiteModel == null) {
            sqlLiteModel = new SqlLiteModel(this);
        }
        sqlLiteModel.initiateController();
    }

    public void GoogleMapInit() {
        if (mapController == null) {
            mapController = new MapController(this);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(mapController);
        }
        mapController.setGoogleData(new MapController.GoogleDataListener() {
            @Override
            public void mapLoaded() {
                //start reversing when ready to go?
                //API connection should send a signal of being done
                mapControllerReverse();
            }

            @Override
            public void markerPlaced() {
                Buttonanimated();
            }
        });
    }

    public void mapControllerReverse() {
        this.mapController.startReverse(getMostData("country", getRecentData()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sqlLiteModel.SQLDestroy();
    }

    public void Buttonanimated() {
        reportTitle.setVisibility(View.VISIBLE);
    }


    public ArrayList<HashMap<String, String>> getRecentData() {
        //Define sorting func
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        arrayList = sqlLiteModel.virusByTime(sharedPref.getOption());
        ArrayList<HashMap<String, String>> sortedArrayList = new ArrayList<>();

        //Since sqlitemodel is sorted by time, just need to parse "And" change virus name to capital

        for (int i = 0; i < arrayList.size(); i++) {
            String virusname = arrayList.get(i).get("virusname").toLowerCase().trim();
            String countryname = arrayList.get(i).get("country").toLowerCase().trim();
            String lastupdated = arrayList.get(i).get("lastupdated");

            //Check if there is bracket word in Virus section and capitalize all letters
            Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
            Matcher matcher = pattern.matcher(virusname);

            while (matcher.find()) {
                if (matcher.group().length() != 0) {
                    String s = matcher.group().trim().substring(1, matcher.group().trim().length() - 1).toUpperCase();
                    virusname = s;
                }
            }

            //Checks if string has AND or ,
            if(countryname.equals("saint vincent and the grenadines")){
                HashMap<String, String> hashMap = new HashMap<>();
                if (!Character.isUpperCase(virusname.charAt(0))) {
                    hashMap.put("virusname", WordUtils.capitalize(virusname));
                } else {
                    hashMap.put("virusname", virusname);
                }
                hashMap.put("country", WordUtils.capitalize(countryname));
                hashMap.put("lastupdated", WordUtils.capitalize(lastupdated));
                sortedArrayList.add(hashMap);
            }
            else if(countryname.equals("trinidad and tobago")){
                HashMap<String, String> hashMap = new HashMap<>();
                if (!Character.isUpperCase(virusname.charAt(0))) {
                    hashMap.put("virusname", WordUtils.capitalize(virusname));
                } else {
                    hashMap.put("virusname", virusname);
                }
                hashMap.put("country", WordUtils.capitalize(countryname));
                hashMap.put("lastupdated", WordUtils.capitalize(lastupdated));
                sortedArrayList.add(hashMap);
            }
            else{

                String dashSplit[] = countryname.split(" - ");
                if(dashSplit.length > 1){
                    String newCountry[] = dashSplit[1].split(" and |, ");
                    if(newCountry.length == 1){
                        HashMap<String, String> hashMap = new HashMap<>();

                        if (!Character.isUpperCase(virusname.charAt(0))) {
                            hashMap.put("virusname", WordUtils.capitalize(virusname));
                        } else {
                            hashMap.put("virusname", virusname);
                        }
                        hashMap.put("country", WordUtils.capitalize(newCountry[0]));
                        hashMap.put("lastupdated", WordUtils.capitalize(lastupdated));
                        sortedArrayList.add(hashMap);
                    }
                    else {
                        for(int j = 0; j < newCountry.length; j++){
                            HashMap<String, String> hashMap = new HashMap<>();

                            if (!Character.isUpperCase(virusname.charAt(0))) {
                                hashMap.put("virusname", WordUtils.capitalize(virusname));
                            } else {
                                hashMap.put("virusname", virusname);
                            }
                            hashMap.put("country", WordUtils.capitalize(newCountry[j]));
                            hashMap.put("lastupdated", WordUtils.capitalize(lastupdated));
                            sortedArrayList.add(hashMap);
                        }
                    }
                }
                else{
                    //if there is no dash, code arrives here
                    //check if it has comma or and
                    String newCountry[] = dashSplit[0].split(" and |, ");
                    if(newCountry.length == 1){
                        HashMap<String, String> hashMap = new HashMap<>();

                        if (!Character.isUpperCase(virusname.charAt(0))) {
                            hashMap.put("virusname", WordUtils.capitalize(virusname));
                        } else {
                            hashMap.put("virusname", virusname);
                        }
                        hashMap.put("country", WordUtils.capitalize(newCountry[0]));
                        hashMap.put("lastupdated", WordUtils.capitalize(lastupdated));
                        sortedArrayList.add(hashMap);
                    }
                    else {
                        for(int j = 0; j < newCountry.length; j++){
                            HashMap<String, String> hashMap = new HashMap<>();

                            if (!Character.isUpperCase(virusname.charAt(0))) {
                                hashMap.put("virusname", WordUtils.capitalize(virusname));
                            } else {
                                hashMap.put("virusname", virusname);
                            }
                            hashMap.put("country", WordUtils.capitalize(newCountry[j]));
                            hashMap.put("lastupdated", WordUtils.capitalize(lastupdated));
                            sortedArrayList.add(hashMap);
                        }
                    }
                }

            }
        }
        return sortedArrayList;
    }

    public ArrayList<HashMap<String, Integer>> getMostData(String s, ArrayList<HashMap<String, String>> inputList){
        //Count how many incident occurred by date
        ArrayList<HashMap<String, Integer>> sortedArrayList = new ArrayList<>();

        //Sorting mechanism
        for(int i = inputList.size()-1; i >= 0 ; i--){
            String virusname = inputList.get(i).get(s);

            if(sortedArrayList.size() == 0){
                HashMap<String, Integer> hashMap = new HashMap<>();
                hashMap.put(WordUtils.capitalize(virusname), 1);
                sortedArrayList.add(hashMap);
            }
            else{
                int size = new Integer(sortedArrayList.size());
                for(int j = 0; j < size; j++){
                    if(sortedArrayList.get(j).containsKey(virusname) || sortedArrayList.get(j).equals(virusname)){
                        sortedArrayList.get(j).put(WordUtils.capitalize(virusname), sortedArrayList.get(j).get(virusname)+1);
                        break;
                    }
                    if(j == size-1 && !sortedArrayList.get(j).containsKey(virusname)){
                        HashMap<String, Integer> hashMap = new HashMap<>();
                        hashMap.put(virusname, 1);
                        sortedArrayList.add(hashMap);
                        break;
                    }
                }
            }
        }

        //Sort sortedArrayList in order of virus occurance
        //bubblesort
        int checkOrder = 0;
        while(checkOrder < sortedArrayList.size() - 1){
            checkOrder = 0;
            for(int i = 0; i < sortedArrayList.size(); i++){
                if(i != sortedArrayList.size()-1){
                    //if current index is bigger than latter index, Collections will swap the elements
                    if(sortedArrayList.get(i).values().iterator().next() < sortedArrayList.get(i+1).values().iterator().next()){
                        Collections.swap(sortedArrayList, i, i + 1);
                    }
                    //if two elements are in order it will increment checkOrder variable
                    //once they are all in order, checkOrder should be sortedArray.size -1
                    else {
                        checkOrder++;
                    }
                }
            }
        }

        return sortedArrayList;

    }
}
