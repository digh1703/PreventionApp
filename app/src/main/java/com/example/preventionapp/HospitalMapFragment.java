package com.example.preventionapp;

import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class HospitalMapFragment extends Fragment implements OnMapReadyCallback {

    private NaverMap naverMap;
    private InfoWindow infoWindow;

    private ArrayList<Marker> markerList;

    private ProgressDialog progressDialog;

    private FusedLocationProviderClient fusedLocationClient;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        View v = inflater.inflate(R.layout.fragment_hospital_map, container, false);

        MapFragment mapFragment = (MapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            this.getChildFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        markerList = new ArrayList<>();

        //진행다일로그 시작
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("잠시 기다려 주세요.");
        progressDialog.setCanceledOnTouchOutside(false);

        return v;
    }

    @Override
    public void onMapReady(@NonNull final NaverMap naverMap) {

        this.naverMap = naverMap;

        //CSV를 파싱한다.
        new HospitalMapTask().execute();

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        //GPS가 켜져있다면 가장 최근위치를 받아와 지도를 이동시킨다.
                        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(
                                new LatLng(location.getLatitude(), location.getLongitude())).animate(CameraAnimation.Easing);
                        naverMap.moveCamera(cameraUpdate);
                    }
                });

        //병원 정보 레이아웃을 세팅한다.
        infoWindow = new InfoWindow();
        infoWindow.setAdapter(new InfoWindow.DefaultViewAdapter(getActivity()) {
            @NonNull
            @Override
            protected View getContentView(@NonNull InfoWindow infoWindow) {
                Marker marker =infoWindow.getMarker();
                Hospital hospital = (Hospital) marker.getTag();
                View view=View.inflate(getActivity(), R.layout.view_info_hospital,null);
                ((TextView) view.findViewById(R.id.name)).setText(hospital.getName());
                ((TextView) view.findViewById(R.id.addr)).setText(hospital.getAddr());
                ((TextView) view.findViewById(R.id.tel)).setText(hospital.getTel());

                return view;
            }
        });
    }

    /**
     * CSV를 스레드를 이용해서 파싱
     */
    public class HospitalMapTask extends AsyncTask<Void, Integer, ArrayList<Hospital>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            clearMap();
        }

        @Override
        protected ArrayList<Hospital> doInBackground(Void... strings) {

            ArrayList<Hospital> hospitalList = new ArrayList<>();

            AssetManager assetManager=getActivity().getAssets();
            BufferedReader br = null;

            try {
                br = new BufferedReader(new InputStreamReader(assetManager.open("hospital.csv"), "MS949"));
                String line = "";

                while((line = br.readLine()) != null){
                    //CSV 1행을 저장하는 리스트
                    List<String> tmpList = new ArrayList<String>();
                    String array[] = line.split(",");
                    //배열에서 리스트 반환

                    if (5 == array.length) {
                        //5개의 정보가 모두 존재하는 병원 정보가 추가한다.
                        try {
                            Hospital hospital = new Hospital(array[0], array[1], array[2],
                                    Double.parseDouble(array[4]), Double.parseDouble(array[3]));

                            hospitalList.add(hospital);
                        }
                        catch (NumberFormatException ex) {

                        }
                    }
                }
                return hospitalList;

            } catch (Exception ex) {
                Log.d("shet", ex.toString());
            }

            return hospitalList;
        }

        @Override
        protected void onPostExecute(ArrayList<Hospital> hospitalList) {
            super.onPostExecute(hospitalList);

            /**
             * CSV파싱이 완료되면 마커를 추가한다.
             */
            for (int i = 0; i < hospitalList.size(); i++) {
                addMarker(hospitalList.get(i));
            }

            progressDialog.dismiss();
        }

        @Override
        protected void onCancelled(ArrayList<Hospital> hospitalList) {
            super.onCancelled(hospitalList);
            progressDialog.dismiss();
        }
    }

    /**
     * 병원 마커를 추가한다.
     * @param hospital
     */
    private void addMarker(Hospital hospital) {
        Marker marker = new Marker();
        marker.setTag(hospital);
        marker.setPosition(new LatLng(hospital.getLat(), hospital.getLon()));
        marker.setIcon(OverlayImage.fromResource(R.drawable.marker));
        marker.setMap(naverMap);
        marker.setOnClickListener(new Overlay.OnClickListener() {
            @Override
            public boolean onClick(@NonNull Overlay overlay) {
                Marker marker=(Marker) overlay;
                infoWindow.open(marker);
                return true;
            }
        });

        markerList.add(marker);
    }

    /**
     * 마커 전부 삭제
     */
    private void clearMap() {

        if(markerList!=null&&markerList.size()>0){
            for(Marker marker : markerList){
                marker.setMap(null);
            }
            markerList.clear();
        }
    }
}
