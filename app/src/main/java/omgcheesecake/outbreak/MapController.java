package omgcheesecake.outbreak;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MapController extends AppCompatActivity implements OnMapReadyCallback {

    private static GoogleMap googleMap;
    private Activity activity;
    private static GoogleDataListener googleDataListener;


    public MapController(Activity activity){
        this.activity = activity;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
        this.googleMap.getUiSettings().setZoomControlsEnabled(false);

        //map is loaded
        googleDataListener.mapLoaded();

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                googleMap.setInfoWindowAdapter(new googlemapInfo(activity, marker.getTitle()));
                return false;
            }
        });

    }

    protected void placeMarker(ArrayList<HashMap<String, LatLng>> s){

        Drawable drawable = null;
        Bitmap icon = null;

        drawable = ContextCompat.getDrawable(activity, R.drawable.outbreakmapmarkerv4);

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0,0,canvas.getWidth(),canvas.getHeight());
        drawable.draw(canvas);
        icon = bitmap;


        for(int i = s.size()-1; i >= 0; i--){
            Iterator<String> IS = s.get(i).keySet().iterator();
            String country= IS.next();
            LatLng latLng = s.get(i).get(country);
            googleMap.addMarker(new MarkerOptions().position(latLng)
                    .title(country)
                    .icon(BitmapDescriptorFactory.fromBitmap(icon)).alpha(0.75f));
            if(i == s.size()-1){
                cameraZOOM(s.get(i).get(country));
            }
        }
        googleDataListener.markerPlaced();

    }

    protected void cameraZOOM(LatLng s){
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(s, 2));
    }

    protected void clearMarkers(){
        googleMap.clear();
    }

    public interface GoogleDataListener{
        void mapLoaded();
        void markerPlaced();
    }

    public void setGoogleData(GoogleDataListener googleDataListener){
        this.googleDataListener = googleDataListener;
    }

    public void startReverse(ArrayList<HashMap<String, Integer>> arrayList){
        ReverseGeoCoding RG = new ReverseGeoCoding(activity);
        RG.execute(arrayList);
    }

    protected class ReverseGeoCoding extends AsyncTask<ArrayList<HashMap<String, Integer>>, Void, ArrayList<HashMap<String, LatLng>>> {
        private Context context;

        public ReverseGeoCoding(Context context){
            this.context = context;
        }

        @Override
        protected ArrayList<HashMap<String, LatLng>> doInBackground(ArrayList<HashMap<String, Integer>>... params) {

            Geocoder geocoder = new Geocoder(this.context, Locale.getDefault());
            List<Address> address;
            int size = params[0].size();

            ArrayList<HashMap<String, LatLng>> arrayListLatLng = new ArrayList<>(size);

            for(int i  = 0; i < size; i++ ){
                String country = params[0].get(i).keySet().iterator().next();
                try {
                    address = geocoder.getFromLocationName(country, 1);
                    if(address != null && address.size() > 0){
                        Address location = address.get(0);
                        LatLng point = new LatLng(location.getLatitude(), location.getLongitude());

                        //Log.i("ROfl",arrayListLatLng.get(i).get("China").toString());
                        HashMap<String, LatLng> temp = new HashMap<>();
                        temp.put(country,point);

                        arrayListLatLng.add(temp);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return arrayListLatLng;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, LatLng>> arrayListLatLng) {
            super.onPostExecute(arrayListLatLng);
            if(arrayListLatLng != null && arrayListLatLng.size() > 0){
                placeMarker(arrayListLatLng);
            }
        }
    }


}
