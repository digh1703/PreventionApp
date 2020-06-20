package com.example.preventionapp;

import android.content.Intent;
import android.content.res.AssetManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.CircleOverlay;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;


public class CrimeMapMurderActivity extends AppCompatActivity implements Overlay.OnClickListener, OnMapReadyCallback, NaverMap.OnCameraChangeListener, NaverMap.OnCameraIdleListener {

    private static final int ACCESS_LOCATION_PERMISSION_REQUEST_CODE=100;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    private List<Marker> markerList=new ArrayList<>();
    private InfoWindow infoWindow;
    private boolean isCameraAnimated=false;
    private FusedLocationProviderClient fusedLocationClient;
    private double myLatitude,myLongitude;

    private Button infoBtn;
    private TextView myLocation;
    private ArrayList<TextView> tList = new ArrayList<>();
    private ArrayList<ProgressBar> pList = new ArrayList<>();
    private int selectedCrime;

    private AppInfo appInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crimemap);

        appInfo = AppInfo.getAppInfo();
        Intent intent = getIntent();
        selectedCrime = intent.getIntExtra("select",-1);

        myLocation = this.findViewById(R.id.activity_crimeMap_myLocation);
        infoBtn = this.findViewById(R.id.activity_crimeMap_btn_info);
        tList.add((TextView)this.findViewById(R.id.activity_crimeMap_first));
        tList.add((TextView)this.findViewById(R.id.activity_crimeMap_second));
        tList.add((TextView)this.findViewById(R.id.activity_crimeMap_third));
        pList.add((ProgressBar)this.findViewById(R.id.activity_crimeMap_firstBar));
        pList.add((ProgressBar)this.findViewById(R.id.activity_crimeMap_secondBar));
        pList.add((ProgressBar)this.findViewById(R.id.activity_crimeMap_thirdBar));

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        /*
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        myLatitude = location.getLatitude();
                        myLongitude = location.getLongitude();
                        myLocation.setText(getCurrentAddress(myLatitude,myLongitude));
                    }
                });
*/
        myLocation.setText(appInfo.getUserData().getResidenceEdit());
        System.out.println(myLatitude+"  "+myLongitude);

        Crime myCrime = getCrime(getjson());
        TreeMap<Integer, String> map = new TreeMap<>();
        map.put(Integer.parseInt(myCrime.getRape()),"강간");
        map.put(Integer.parseInt(myCrime.getMurder()),"살인");
        map.put(Integer.parseInt(myCrime.getRobbery()),"강도");
        map.put(Integer.parseInt(myCrime.getLarceny()),"절도");
        map.put(Integer.parseInt(myCrime.getViolence()),"폭행");
        Iterator<Integer> iterator = map.descendingKeySet().iterator();
        int s;
        for(int i=0;i<3;i++){
            s = iterator.next();
            tList.get((i)).setText(map.get(s));
            pList.get((i)).setProgress(s);
           // System.out.println(s+"  "+Integer.parseInt(s));
        }

        infoBtn.setText(tList.get(0).getText()+" 범죄 정보 확인");
        infoBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CrimeMapMurderActivity.this, PreventionInfoContentsActivity.class);
                intent.putExtra("clickGroupItem",0);
                intent.putExtra("clickChildItem",0);
                CrimeMapMurderActivity.this.startActivity(intent);
            }
        });

        MapFragment mapFragment=(MapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        FusedLocationSource locationSource=new FusedLocationSource(this,100);
        naverMap.setLocationSource(locationSource);
        UiSettings uiSettings=naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);

        LatLng mapCenter=naverMap.getCameraPosition().target;
        CameraPosition cameraPosition=new CameraPosition(mapCenter,12.5);
        naverMap.setCameraPosition(cameraPosition);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        //GPS가 켜져있다면 가장 최근위치를 받아와 지도를 이동시킨다.
                        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(
                                new LatLng(location.getLatitude(), location.getLongitude())).animate(CameraAnimation.Easing);
                        CrimeMapMurderActivity.this.naverMap.moveCamera(cameraUpdate);
                    }
                });

        //getJson 원래위치

        resetMarkerList();
        markerset(getjson());

        infoWindow=new InfoWindow();
        infoWindow.setAdapter(new InfoWindow.DefaultViewAdapter(this) {
            @NonNull
            @Override
            protected View getContentView(@NonNull InfoWindow infoWindow) {
                Marker marker =infoWindow.getMarker();
                Crime crime=(Crime) marker.getTag();
                View view=View.inflate(CrimeMapMurderActivity.this, R.layout.view_info_window_crimedata,null);
                ((TextView) view.findViewById(R.id.name)).setText(crime.getName());
                switch (selectedCrime){
                    case 0:
                        ((TextView) view.findViewById(R.id.murder)).setText("살인: "+crime.getMurder());
                        break;
                    case 1:
                        ((TextView) view.findViewById(R.id.murder)).setText("강도: "+crime.getRobbery());
                        break;
                    case 2:
                        ((TextView) view.findViewById(R.id.murder)).setText("강간: "+crime.getRape());
                        break;
                    case 3:
                        ((TextView) view.findViewById(R.id.murder)).setText("절도: "+crime.getLarceny());
                        break;
                    case 4:
                        ((TextView) view.findViewById(R.id.murder)).setText("폭행: "+crime.getViolence());
                        break;
                    default:
                        break;
                }

                return view;
            }
        });

    }

    private JSONArray getjson(){
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
            return jsonArray;
        } catch (IOException e) {e.printStackTrace();}
        catch (JSONException e) {e.printStackTrace(); }
        return null;
    }

    @Override
    public void onCameraChange(int reason, boolean animated) {
        isCameraAnimated=animated;
    }

    @Override
    public void onCameraIdle() { }
    private void markerset(JSONArray jsonArray){
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo = jsonArray.getJSONObject(i);
                Crime crimedata = new Crime();

                crimedata.setLat(jo.getDouble("latitude"));
                crimedata.setLng(jo.getDouble("longtitude"));
                crimedata.setName(jo.getString("id"));
                crimedata.setMurder(jo.getString("murder"));
                crimedata.setRobbery(jo.getString("robbery"));
                crimedata.setRape(jo.getString("rape"));
                crimedata.setLarceny(jo.getString("larceny"));
                crimedata.setViolence(jo.getString("violence"));

                Marker marker = new Marker();
                CircleOverlay circle=new CircleOverlay();
                marker.setTag(crimedata);
                marker.setPosition(new LatLng(crimedata.getLat(), crimedata.getLng()));
                marker.setIcon(OverlayImage.fromResource(R.drawable.marker));
                circle.setCenter(new LatLng(crimedata.getLat(),crimedata.getLng()));

                switch (selectedCrime){
                    case 0:
                        if(Integer.parseInt(crimedata.getMurder())<4){
                            circle.setRadius(800);
                            circle.setColor(0x8016AA52);
                        }
                        if(Integer.parseInt(crimedata.getMurder())>=4&&Integer.parseInt(crimedata.getMurder())<10){
                            circle.setRadius(1200);
                            circle.setColor(0x80FEE134);
                        }
                        if(Integer.parseInt(crimedata.getMurder())>=10&&Integer.parseInt(crimedata.getMurder())<20){
                            circle.setRadius(1600);
                            circle.setColor(0x80ED9149);
                        }
                        if(Integer.parseInt(crimedata.getMurder())>20){
                            circle.setRadius(2000);
                            circle.setColor(0x80F15B5B);
                        }
                        break;
                    case 1:
                        if(Integer.parseInt(crimedata.getRobbery())<4){
                            circle.setRadius(800);
                            circle.setColor(0x8016AA52);
                        }
                        if(Integer.parseInt(crimedata.getRobbery())>=4&&Integer.parseInt(crimedata.getRobbery())<10){
                            circle.setRadius(1200);
                            circle.setColor(0x80FEE134);
                        }
                        if(Integer.parseInt(crimedata.getRobbery())>=10&&Integer.parseInt(crimedata.getRobbery())<20){
                            circle.setRadius(1600);
                            circle.setColor(0x80ED9149);
                        }
                        if(Integer.parseInt(crimedata.getRobbery())>20){
                            circle.setRadius(2000);
                            circle.setColor(0x80F15B5B);
                        }
                        break;
                    case 2:
                        if(Integer.parseInt(crimedata.getRape())<50){
                            circle.setRadius(800);
                            circle.setColor(0x8016AA52);
                        }
                        if(Integer.parseInt(crimedata.getRape())>=50&&Integer.parseInt(crimedata.getRape())<200){
                            circle.setRadius(1200);
                            circle.setColor(0x80FEE134);
                        }
                        if(Integer.parseInt(crimedata.getRape())>=200&&Integer.parseInt(crimedata.getRape())<500){
                            circle.setRadius(1600);
                            circle.setColor(0x80ED9149);
                        }
                        if(Integer.parseInt(crimedata.getRape())>500){
                            circle.setRadius(2000);
                            circle.setColor(0x80F15B5B);
                        }
                        break;
                    case 3:
                        if(Integer.parseInt(crimedata.getLarceny())<500){
                            circle.setRadius(800);
                            circle.setColor(0x8016AA52);
                        }
                        if(Integer.parseInt(crimedata.getLarceny())>=500&&Integer.parseInt(crimedata.getLarceny())<1000){
                            circle.setRadius(1200);
                            circle.setColor(0x80FEE134);
                        }
                        if(Integer.parseInt(crimedata.getLarceny())>=1000&&Integer.parseInt(crimedata.getLarceny())<3000){
                            circle.setRadius(1600);
                            circle.setColor(0x80ED9149);
                        }
                        if(Integer.parseInt(crimedata.getLarceny())>3000){
                            circle.setRadius(2000);
                            circle.setColor(0x80F15B5B);
                        }
                        break;
                    case 4:
                        if(Integer.parseInt(crimedata.getViolence())<800){
                            circle.setRadius(800);
                            circle.setColor(0x8016AA52);
                        }
                        if(Integer.parseInt(crimedata.getViolence())>=800&&Integer.parseInt(crimedata.getViolence())<2500){
                            circle.setRadius(1200);
                            circle.setColor(0x80FEE134);
                        }
                        if(Integer.parseInt(crimedata.getViolence())>=2500&&Integer.parseInt(crimedata.getViolence())<5000){
                            circle.setRadius(1600);
                            circle.setColor(0x80ED9149);
                        }
                        if(Integer.parseInt(crimedata.getViolence())>5000){
                            circle.setRadius(2000);
                            circle.setColor(0x80F15B5B);
                        }
                        break;
                    default:
                        break;
                }

                circle.setMap(naverMap);
                marker.setMap(naverMap);
                marker.setOnClickListener(this);
                markerList.add(marker);
            }
        }catch(JSONException e){e.printStackTrace();}

    }
    private void resetMarkerList(){
        if(markerList!=null&&markerList.size()>0){
            for(Marker marker : markerList){
                marker.setMap(null);
            }
            markerList.clear();
        }
    }

    @Override
    public boolean onClick(@NonNull Overlay overlay) {
        Marker marker=(Marker) overlay;
        infoWindow.open(marker);
        return false;
    }

    private Crime getCrime(JSONArray jsonArray){
        Crime crimedata = new Crime();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo = jsonArray.getJSONObject(i);
                if(jo.getString("id").equals(appInfo.getUserData().getResidenceEdit())){
                    crimedata.setLat(jo.getDouble("latitude"));
                    crimedata.setLng(jo.getDouble("longtitude"));
                    crimedata.setName(jo.getString("id"));
                    crimedata.setMurder(jo.getString("murder"));
                    crimedata.setRobbery(jo.getString("robbery"));
                    crimedata.setRape(jo.getString("rape"));
                    crimedata.setLarceny(jo.getString("larceny"));
                    crimedata.setViolence(jo.getString("violence"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return crimedata;
    }


    public String getCurrentAddress( double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }
        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        }
        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";
    }
}