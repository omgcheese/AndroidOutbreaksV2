package omgcheesecake.outbreak;

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

//Main Model
//This model should be shared with other models as starting
//However other models should not all connected; Use implements

public class MapModel extends AppCompatActivity{

    private static TextView reportTitle;
//    private ApiModel apiModel;
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

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
                reportModel.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_NoActionBar_TranslucentDecor);
                reportModel.show(fm, "outbreak_report");
                return false;
            }
        });
    }

    public void SQLInit(){
        if (this.sqlLiteModel == null){
            sqlLiteModel = new SqlLiteModel(this);
        }
        sqlLiteModel.initiateController();
    }

    public void GoogleMapInit(){
        if(mapController == null){
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

    public void mapControllerReverse(){
        this.mapController.startReverse(sqlLiteModel.virusByTime(sharedPref.getOption()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sqlLiteModel.SQLDestroy();
    }

    public void Buttonanimated(){
        reportTitle.setVisibility(View.VISIBLE);
    }
}
