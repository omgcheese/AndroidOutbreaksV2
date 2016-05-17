package omgcheesecake.outbreak;

import android.app.Activity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class googlemapInfo implements GoogleMap.InfoWindowAdapter {

    private final View myView;
    private Activity activity;
    private String country;
    private SqlLiteModel sqlLiteModel;
    private SharedPref sharedPref;

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        addingInfo(reformate(SearchingVirus()));
        return this.myView;
    }

    googlemapInfo(Activity activity, String country){
        this.activity = activity;
        this.myView = activity.getLayoutInflater().inflate(R.layout.googlemapinfo, null);
        this.country = country;
        this.sqlLiteModel = new SqlLiteModel(activity);
        sqlLiteModel.initiateController();
    }

    public ArrayList<HashMap<String,String>> SearchingVirus(){
        ArrayList<HashMap<String,String>> arrayList = sqlLiteModel.getVirusesfromCountry(this.country);
        ArrayList<HashMap<String,String>> result = CheckChar(arrayList);
        return result;
    }

    public HashMap<String, Integer> reformate(ArrayList<HashMap<String,String>> s){
        HashMap<String,Integer> result = new HashMap<>();
        for(int i = 0; i < s.size(); i++){
            //does result has s.get(i).get("virusname")?

            //if it does, increase count
            if(result.containsKey(s.get(i).get("virusname"))){
                result.put(s.get(i).get("virusname"), result.get(s.get(i).get("virusname"))+1);
            }
            //add such virus into result
            else{
                result.put(s.get(i).get("virusname"), 1);
            }
        }
        return result;
    }

    public void addingInfo(HashMap<String, Integer> result){
        TableLayout tableLayout = (TableLayout)this.myView.findViewById(R.id.googleInfoWindow);

        Iterator<String> v = result.keySet().iterator();
        for(int i = 0; i < result.size(); i++){

            String virus = new String(v.next());
            TableRow tr = new TableRow(this.activity);
            TextView tv1 = new TextView(this.activity);
            TextView tv2 = new TextView(this.activity);

            //Setting Text
            tv1.setText(virus);
            tv2.setText(String.valueOf(result.get(virus)));

            //Setting columns for respective textviews
            tv1.setLayoutParams(new TableRow.LayoutParams(1));
            tv2.setLayoutParams(new TableRow.LayoutParams(2));

            tv1.setMaxWidth(300);
            tv2.setMaxWidth(100);

            //17 is Center and 5 is right
            tv1.setGravity(17);
            tv2.setGravity(5);

            tr.addView(tv1);
            tr.addView(tv2);

            tableLayout.addView(tr);
        }

    }

    public ArrayList<HashMap<String, String>> CheckChar(ArrayList<HashMap<String,String>> sa) {

        //Define sorting func
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        arrayList = sa;
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

//            if(countryname.equals(this.country.toLowerCase())){
//                HashMap<String, String> hashMap = new HashMap<>();
//                if (!Character.isUpperCase(virusname.charAt(0))) {
//                    hashMap.put("virusname", WordUtils.capitalize(virusname));
//                } else {
//                    hashMap.put("virusname", virusname);
//                }
//                hashMap.put("country", WordUtils.capitalize(countryname));
//                hashMap.put("lastupdated", WordUtils.capitalize(lastupdated));
//                sortedArrayList.add(hashMap);
//            }

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
                    //check if searched country is index of 0 or 1
                    //if the country is index of 0
                    //check if there is comma or and in second dash Split
                    if(dashSplit[0].trim().equals(this.country.toLowerCase())){
                        //we dont care if the searched country is at first index.
                    }
                    else {
                        //check if it has comma or and
                        String newCountry[] = dashSplit[1].split(" and |, ");
                        if(newCountry.length == 1){
                            if(newCountry[0].trim().equals(this.country.trim().toLowerCase())){
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
                        }
                        else {
                            for(int j = 0; j < newCountry.length; j++){
                                if(newCountry[j].trim().equals(this.country.trim().toLowerCase())){
                                    HashMap<String, String> hashMap = new HashMap<>();

                                    if (!Character.isUpperCase(virusname.charAt(0))) {
                                        hashMap.put("virusname", WordUtils.capitalize(virusname));
                                    } else {
                                        hashMap.put("virusname", virusname);
                                    }
                                    hashMap.put("country", WordUtils.capitalize(countryname));
                                    hashMap.put("lastupdated", WordUtils.capitalize(lastupdated));
                                    sortedArrayList.add(hashMap);
                                    break;
                                }
                            }
                        }
                    }
                }
                else{
                    //check if it has comma or and
                    String newCountry[] = dashSplit[0].split(" and |, ");
                    if(newCountry.length == 1){
                        if(newCountry[0].trim().equals(this.country.trim().toLowerCase())){
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
                    }
                    else {
                        for(int j = 0; j < newCountry.length; j++){
                            if(newCountry[j].trim().equals(this.country.trim().toLowerCase())){
                                HashMap<String, String> hashMap = new HashMap<>();

                                if (!Character.isUpperCase(virusname.charAt(0))) {
                                    hashMap.put("virusname", WordUtils.capitalize(virusname));
                                } else {
                                    hashMap.put("virusname", virusname);
                                }
                                hashMap.put("country", WordUtils.capitalize(countryname));
                                hashMap.put("lastupdated", WordUtils.capitalize(lastupdated));
                                sortedArrayList.add(hashMap);
                                break;
                            }
                        }
                    }
                }

            }
        }
        return sortedArrayList;
    }
}
