package com.example.preventionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.Bundle;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CrimeMap extends AppCompatActivity implements OnMapReadyCallback,NaverMap.OnCameraChangeListener, NaverMap.OnCameraIdleListener {

    private static final int ACCESS_LOCATION_PERMISSION_REQUEST_CODE=100;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    private List<Marker> markerList=new ArrayList<>();
    private boolean isCameraAnimated=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_map);

        MapFragment mapFragment=(MapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        FusedLocationSource locationSource=new FusedLocationSource(this,100);
        naverMap.setLocationSource(locationSource);
        UiSettings uiSettings=naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);

        LatLng mapCenter=naverMap.getCameraPosition().target;

        getjson();


    }

    private void getjson(){

        AssetManager assetManager=getAssets();

        try {
            InputStream is= assetManager.open("jsons/crime.json");
            InputStreamReader isr= new InputStreamReader(is);
            BufferedReader reader= new BufferedReader(isr);

            StringBuffer buffer= new StringBuffer();
            String line= reader.readLine();
            while (line!=null){
                buffer.append(line+"\n");
                line=reader.readLine();
            }

            String jsonData= buffer.toString();

            JSONArray jsonArray= new JSONArray(jsonData);

            resetMarkserList();
            System.out.println(jsonArray.length());

            markerset(jsonArray);


        } catch (IOException e) {e.printStackTrace();}
        catch (JSONException e) {e.printStackTrace(); }


    }

    @Override
    public void onCameraChange(int reason, boolean animated) {
        isCameraAnimated=animated;
    }

    @Override
    public void onCameraIdle() {


    }

    private void markerset(JSONArray jsonArray){

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                System.out.println(i);
                JSONObject jo = jsonArray.getJSONObject(i);
                Crime crimedata = new Crime();
                List<Double> latitude=new ArrayList<>();
                List<Double> longtitude=new ArrayList<>();

                crimedata.setLat(jo.getDouble("latitude"));
                crimedata.setLng(jo.getDouble("longtitude"));


                Marker marker = new Marker();
                marker.setPosition(new LatLng(crimedata.getLat(), crimedata.getLng()));
                marker.setIcon(OverlayImage.fromResource(R.drawable.marker_green));
                marker.setMap(naverMap);
                markerList.add(marker);


            }

        }catch(JSONException e){e.printStackTrace();}

    }
    private void resetMarkserList(){
        if(markerList!=null&&markerList.size()>0){
            for(Marker marker : markerList){
                marker.setMap(null);
            }
            markerList.clear();
        }
    }


}
