package omgcheesecake.outbreak;


import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportModel extends DialogFragment {

    private Toolbar toolbar;
    private Dialog dialog;
    private SharedPref sharedPref;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RecyclerView recyclerView;
    private RecycleList recycleList;
    private SqlLiteModel sqlLiteModel;
    private View view;

    private RadioGroup radioGroup;

    private RadioButton recent;
    private RadioButton most;
    private RadioButton region;

    public ReportModel(){
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ReportModel newInstance(){
        ReportModel reportModel = new ReportModel();
        return reportModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.outbreak_report, container, false);
        this.view = view;
        setHasOptionsMenu(true);

        //Toolbar Init
        toolBarInit(view);

        //tabBarInit
        tabBarInit(view);


//        //Init outbreak list
//        RecycleInit();

        LoadDataToView(view);

//        PieChart(view);

        return view;
    }

    public void tabBarInit(View view){
        if(radioGroup == null){
            radioGroup = (RadioGroup)view.findViewById(R.id.radioGroup);
            recent = (RadioButton)view.findViewById(R.id.Recent);
            most = (RadioButton)view.findViewById(R.id.Most);
            region = (RadioButton)view.findViewById(R.id.Region);
        }

        recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadDataToView(v);
            }
        });

        most.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadDataToView(v);
            }
        });

        region.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadDataToView(v);
            }
        });
    }

    public void LoadDataToView(View view){

        int selectedINT = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton)view.findViewById(selectedINT);

        if(radioButton.getText().equals("Recent")){
//            recycleList = new RecycleList(sqlLiteModel.virusByTime(sharedPref.getOption()));
            recycleList = new RecycleList(getRecentData());

        }
        else if(radioButton.getText().equals("Most")){
            recycleList = new RecycleList(getMostData("virusname", getRecentData()), "Most");
        }
        else{
            recycleList = new RecycleList(getMostData("country", getRecentData()), "Region");
        }

        if(recyclerView == null){
            recyclerView = (RecyclerView)view.findViewById(R.id.outbreakList);
        }

        recycleList.notifyDataSetChanged();
        recyclerView.setAdapter(recycleList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    public ArrayList<HashMap<String, String>> getRecentData(){
        //INit Sqllite
        if(sqlLiteModel == null){
            sqlLiteModel = new SqlLiteModel(getActivity());
        }
        //init sharedpref
        if(sharedPref == null){
            sharedPref = new SharedPref(getActivity());
        }

        //Define sorting func
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        arrayList = sqlLiteModel.virusByTime(sharedPref.getOption());
        ArrayList<HashMap<String, String>> sortedArrayList = new ArrayList<>();

        //Since sqlitemodel is sorted by time, just need to parse "And" change virus name to capital

        for(int i = 0; i < arrayList.size(); i++){
            String virusname = arrayList.get(i).get("virusname").toLowerCase().trim();
            String countryname = arrayList.get(i).get("country").toLowerCase().trim();
            String lastupdated = arrayList.get(i).get("lastupdated");

            //Check if there is bracket word in Virus section and capitalize all letters
            Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
            Matcher matcher = pattern.matcher(virusname);

            while(matcher.find()){
                if(matcher.group().length() != 0){
                    String s = matcher.group().trim().substring(1, matcher.group().trim().length()-1).toUpperCase();
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
                String newCountry[] = countryname.split(" - | and |, ");
                for (int j = 0; j < newCountry.length; j++) {
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
        return sortedArrayList;
    }

    public ArrayList<HashMap<String, Integer>> getMostData(String s, ArrayList<HashMap<String, String>> inputList){
        //Count how many incident occurred by date

        //INit Sqllite
        if(sqlLiteModel == null){
            sqlLiteModel = new SqlLiteModel(getActivity());
        }
        //init sharedpref
        if(sharedPref == null){
            sharedPref = new SharedPref(getActivity());
        }

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

    public void toolBarInit(View view){

        if(toolbar == null){
            toolbar = (Toolbar)view.findViewById(R.id.toolbar);
        }
        toolbar.setTitle(sharedPref.getOption() + " Outbreak Report");

        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_outbreak_model, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case (android.R.id.home):
                dialog.dismiss();
                break;

            case (R.id.action_1week):
                sharedPref.setOption("1 Week");
                break;

            case (R.id.action_1month):
                sharedPref.setOption("1 Month");
                break;

            case (R.id.action_3month):
                sharedPref.setOption("3 Month");
                break;

            case (R.id.action_6month):
                sharedPref.setOption("6 Month");
                break;

            case (R.id.action_1year):
                sharedPref.setOption("1 Year");
                break;
        }
        LoadDataToView(this.view);
        toolbarSetTitle();
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        return dialog;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = new SharedPref(getActivity());

        sqlLiteModel = new SqlLiteModel(getActivity());
        sqlLiteModel.initiateController();
    }

    public void toolbarSetTitle(){
        toolbar.setTitle(sharedPref.getOption() + " Outbreak Report");
    }


//Disabling graph view for now
//    public void PieChart(View view){
//        //Init Line chart
//        //Make sure touch is not enabled
//        PieChart pieChart = (PieChart)view.findViewById(R.id.PieChart);
//        pieChart.setTouchEnabled(false);
//        pieChart.setDescription("");
//        pieChart.setUsePercentValues(true);
//
//        //enable Hole
//        pieChart.setDrawHoleEnabled(true);
//        pieChart.setHoleRadius(7);
//        pieChart.setTransparentCircleRadius(10);
//
//        //Disabling Legend
//        Legend legend = pieChart.getLegend();
//        legend.setEnabled(false);
////        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
//
//        //Needs to populate List
//        //Count how many incident occurred by date
//
//        //INit Sqllite
//        if(sqlLiteModel == null){
//            sqlLiteModel = new SqlLiteModel(getActivity());
//        }
//        //init sharedpref
//        if(sharedPref == null){
//            sharedPref = new SharedPref(getActivity());
//        }
//
//        //Define sorting func
//        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
//        arrayList = sqlLiteModel.virusByTime(sharedPref.getOption());
//        ArrayList<HashMap<String, Integer>> sortedArrayList = new ArrayList<>();
//
//        //Sorting mechanism
//        for(int i = arrayList.size()-1; i >= 0 ; i--){
//            String virusname = arrayList.get(i).get("virusname").toLowerCase().trim();
//
//            if(sortedArrayList.size() == 0){
//                HashMap<String, Integer> hashMap = new HashMap<>();
//                hashMap.put(virusname.toLowerCase().trim(), 1);
//                sortedArrayList.add(hashMap);
//            }
//            else{
//                int size = new Integer(sortedArrayList.size());
//                for(int j = 0; j < size; j++){
//                    if(sortedArrayList.get(j).containsKey(virusname.toLowerCase().trim()) || sortedArrayList.get(j).equals(virusname.toLowerCase().trim())){
//                        sortedArrayList.get(j).put(virusname.toLowerCase().trim(), sortedArrayList.get(j).get(virusname.toLowerCase().trim())+1);
//                        break;
//                    }
//                    if(j == size-1 && !sortedArrayList.get(j).containsKey(virusname.trim())){
//                        HashMap<String, Integer> hashMap = new HashMap<>();
//                        hashMap.put(virusname.toLowerCase().trim(), 1);
//                        sortedArrayList.add(hashMap);
//                        break;
//                    }
//                }
//            }
//        }
//
//        //Parse bracket
//        //Replacing Long virus name with Abbrevation
//        for(int i = 0; i < sortedArrayList.size(); i++){
//            String LongVirusName = sortedArrayList.get(i).keySet().iterator().next();
//
//            //[] - Any Characters you want
//            //[^] any character that is not
//            //\\s match white spaces
//            //\\S match non-white spaces
//
//            //word that starts with bracket
//            //[(]
//
//            Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
//            Matcher matcher = pattern.matcher(LongVirusName);
//
//            while(matcher.find()){
//                if(matcher.group().length() != 0){
//                    sortedArrayList.get(i).put(matcher.group().trim().substring(1, matcher.group().trim().length()-1).toUpperCase(), sortedArrayList.get(i).get(LongVirusName));
//                    sortedArrayList.get(i).remove(LongVirusName);
//                }
//            }
//
//        }
//
//        //Sort sortedArrayList in order of virus occurance
//        //bubblesort
//        int checkOrder = 0;
//        while(checkOrder < sortedArrayList.size() - 1){
//            checkOrder = 0;
//            for(int i = 0; i < sortedArrayList.size(); i++){
//                if(i != sortedArrayList.size()-1){
//                    //if current index is bigger than latter index, Collections will swap the elements
//                    if(sortedArrayList.get(i).values().iterator().next() > sortedArrayList.get(i+1).values().iterator().next()){
//                        Collections.swap(sortedArrayList, i, i + 1);
//                    }
//                    //if two elements are in order it will increment checkOrder variable
//                    //once they are all in order, checkOrder should be sortedArray.size -1
//                    else {
//                        checkOrder++;
//                    }
//                }
//            }
//        }
//
//        //Limit sorted ArrayList as 8 and last element should be Other
//        if(sortedArrayList.size() > 6){
//            int sumIncident = 0;
//            for(int i = 6; i < sortedArrayList.size(); i++){
//                sumIncident = sumIncident + sortedArrayList.get(i).values().iterator().next();
//            }
//            for(int i = 6; i < sortedArrayList.size(); i++){
//                sortedArrayList.remove(i);
//            }
//            HashMap<String, Integer> hashMap = new HashMap<>();
//            hashMap.put("Other", sumIncident);
//            sortedArrayList.add(0, hashMap);
//        }
//
//
//
//
//        //Add x values
//        ArrayList<String> xVals = new ArrayList<>();
//        for(int i = sortedArrayList.size()-1; i >= 0; i--){
//            xVals.add(sortedArrayList.get(i).keySet().iterator().next());
//        }
//
//
//        //add y values
//        ArrayList<Entry> yVals = new ArrayList<>();
//        for(int i = sortedArrayList.size()-1; i >= 0; i--){
//            Entry entry = new Entry(sortedArrayList.get(i).values().iterator().next(), i);
//            yVals.add(entry);
//        }
//
//        //Define pie data set
//        PieDataSet pieDataSet = new PieDataSet(yVals, "Outbreaks");
//
//        //Add color
//        ArrayList<Integer> colors = new ArrayList<>();
//
//        for (int c : ColorTemplate.VORDIPLOM_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.JOYFUL_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.COLORFUL_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.LIBERTY_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.PASTEL_COLORS)
//            colors.add(c);
//
//        colors.add(ColorTemplate.getHoloBlue());
//        pieDataSet.setColors(colors);
//
//
//        //setting pie data using xVals and yVals
//        PieData pieData = new PieData(xVals, pieDataSet);
//
//        //setting pieData config
//        pieData.setValueFormatter(new PercentFormatter());
//        pieData.setDrawValues(false);
//
//        //set Piechart data
//        pieChart.setData(pieData);
//
//        //refresh the data
//        pieChart.invalidate();
//    }

}
