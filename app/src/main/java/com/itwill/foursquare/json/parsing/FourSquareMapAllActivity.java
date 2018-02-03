package com.itwill.foursquare.json.parsing;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FourSquareMapAllActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<FourSquare> fourSquareList;
    TextView mapTV;
    TextView FourSquareTV;
    ImageView mapIV;
    ArrayList<LatLng> latLngList;
    private LocationManager locationManager;
    private MyLocationListener myLocationListener;
    private boolean status = false;
    private String currentAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four_square_map_all);

        fourSquareList = getIntent().getParcelableArrayListExtra("fourSquareList");
        for (int i = 0; i < fourSquareList.size(); i++){
            Log.e("bitmap", fourSquareList.get(i).getBitmap().toString());
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
        mapTV = (TextView) findViewById(R.id.mapTV);
        mapIV = (ImageView) findViewById(R.id.mapIV);
        FourSquareTV = (TextView) findViewById(R.id.FourSquareTV);

        mapTV.setText(fourSquareList.get(fourSquareList.size() - 1).getAddress());
        mapIV.setImageBitmap(fourSquareList.get(fourSquareList.size() - 1).getBitmap());
        FourSquareTV.setText(fourSquareList.get(fourSquareList.size() - 1).getName());



    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        double initLat = 0;
        double initLng = 0;
        latLngList = new ArrayList<LatLng>();
        for (int i = 0; i < fourSquareList.size(); i++) {
            initLat += Double.parseDouble(fourSquareList.get(i).getLat());
            initLng += Double.parseDouble(fourSquareList.get(i).getLng());
        }
        LatLng initialLatLng = new LatLng(initLat / fourSquareList.size(), initLng / fourSquareList.size());

        for (int i = 0; i < fourSquareList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(
                    Double.parseDouble(fourSquareList.get(i).getLat()),
                    Double.parseDouble(fourSquareList.get(i).getLng())
            );
            markerOptions.position(latLng);
            markerOptions.title(fourSquareList.get(i).getName());
            markerOptions.snippet(fourSquareList.get(i).getAddress());
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.dining_icon));
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.radius(100);
            circleOptions.center(latLng);
            circleOptions.fillColor(Color.argb(25, 255, 255, 0));
            circleOptions.strokeColor(Color.YELLOW);
            mMap.addCircle(circleOptions);
            mMap.addMarker(markerOptions).showInfoWindow();
        }


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLatLng, 16));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);


        /*********************** Map Click ***********************/
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });
        /*********************** Map long Click ***********************/
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                String url = "";
                String address = "";
                LatLng latLng = marker.getPosition();
                for (int i = 0; i < fourSquareList.size(); i++) {
                    if(Double.parseDouble(fourSquareList.get(i).getLat()) == latLng.latitude
                            && Double.parseDouble(fourSquareList.get(i).getLng()) == latLng.longitude){
                        url = fourSquareList.get(i).getUrl();
                        address = fourSquareList.get(i).getAddress();
                    }
                }
                if(url.equals("없음")){
                    intent.putExtra("query", address);
                }else{
                    intent.putExtra("query", url);
                }
                startActivity(intent);
            }
        });

        /*********************** Map Marker Click ***********************/
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng latLng = marker.getPosition();
                for (int i = 0; i < fourSquareList.size(); i++) {
                    if(Double.parseDouble(fourSquareList.get(i).getLat()) == latLng.latitude
                            && Double.parseDouble(fourSquareList.get(i).getLng()) == latLng.longitude){
                        mapTV.setText(fourSquareList.get(i).getAddress());
                        mapIV.setImageBitmap(fourSquareList.get(i).getBitmap());
                        FourSquareTV.setText(fourSquareList.get(i).getName());
                    }
                }
                if (status == true) {
                    marker.hideInfoWindow();
                    status = false;
                } else if (status == false) {
                    marker.showInfoWindow();
                    status = true;
                }

                return status;
            }
        });
    }

    private void displayLocationInfo(Location location) {
        StringBuffer sb = new StringBuffer();
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.KOREA);
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addressList == null) {
                throw new Exception("NO ADDRESS");
            }
            Address address = addressList.get(0);
            String addressStr = address.getAddressLine(0);
            currentAddress = addressStr;
            sb.append(addressStr);
        } catch (Exception e) {
            sb.append("NO ADDRESS : " + e.getMessage());
        }
        mapTV.setText(sb.toString());
    }

    /****************************************/
    public class MyLocationListener implements LocationListener {
        //위치정보 보여주기
        //구글 맵 이동

        @Override
        public void onLocationChanged(Location location) {
            //위치정보보여주기
            displayLocationInfo(location);
            //구글맵이동
            LatLng newLatLng =
                    new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(newLatLng);
            //mMap.moveCamera(cameraUpdate);
            mMap.animateCamera(cameraUpdate);

            /*
            Marker 지우기
             */
            mMap.clear();

            /*
            Marker 그리기
             */
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(newLatLng);
            markerOptions.title("현재 위치");
            if (currentAddress == null || currentAddress.equals("")) {
                currentAddress = "No Address";
            }
            markerOptions.snippet(currentAddress);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.dining_icon));
            mMap.addMarker(markerOptions);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }
    /****************************************/
}
