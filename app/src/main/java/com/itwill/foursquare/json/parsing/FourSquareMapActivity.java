package com.itwill.foursquare.json.parsing;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FourSquareMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String currentAddress;
    private boolean status = false;
    private ArrayList<LatLng> arrayPoints;
    TextView mapTV;
    TextView FourSquareTV;
    ImageView mapIV;
    ImageButton findMyLocationB;
    TextView tipsTV;
    private String name;
    private String url;
    private String address;
    private String lat;
    private String lng;
    private String tips;
    private LatLng startLatLng;
    private LatLng endLatLng;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four_square_map);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        url = intent.getStringExtra("url");
        address = intent.getStringExtra("address");
        lat = intent.getStringExtra("lat");
        lng = intent.getStringExtra("lng");
        tips = intent.getStringExtra("tips");
        Bitmap bitmap = intent.getParcelableExtra("bitmap");
        Log.e("sq", name + ", " + url + ", " + address + ", " + lat + ", " + lng + ", " + bitmap.toString() + ", " + tips);
        SupportMapFragment mapView = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        mapView.getMapAsync(this);
        mapTV = (TextView) findViewById(R.id.mapTV);
        mapIV = (ImageView) findViewById(R.id.mapIV);
        FourSquareTV = (TextView) findViewById(R.id.FourSquareTV);
        tipsTV = (TextView) findViewById(R.id.tipsTV);
        FourSquareTV.setText(name);
        mapTV.setText(address);
        mapIV.setImageBitmap(bitmap);
        tipsTV.setText("꿀팁 : " + tips);
        findMyLocationB = (ImageButton) findViewById(R.id.findMyLocationB);
        findMyLocationB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMyLocation();
            }
        });
    }

    public void getMyLocation() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        long minTime = 3000;
        float minDistance = 10;

        MyLocationListener listener = new MyLocationListener();

        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime,
                minDistance, listener);
    }

    private void appendText(String msg) {
        mapTV.append("\n" + msg);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        /*
        초기위치 설정
         */
        LatLng initialLatLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        /*MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(initialLatLng);
        markerOptions.title("여삼빌딩");*/
        /*
        지도를 마커위치로 이동해야함
         */
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLatLng, 17));



        /*
        지도타입
         */
        //mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//        mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
//        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
//        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL | GoogleMap.MAP_TYPE_TERRAIN);

        /*
        지도 UI 설정 ZOOM 레벨 설정
         */
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);

        MarkerOptions markerOptions = new MarkerOptions();
        LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        markerOptions.position(latLng);
        markerOptions.title(name);
        markerOptions.snippet(address);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.dining_icon));
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.radius(100);
        circleOptions.center(latLng);
        circleOptions.fillColor(Color.argb(25, 255, 255, 0));
        circleOptions.strokeColor(Color.YELLOW);
        mMap.addCircle(circleOptions);
        mMap.addMarker(markerOptions).showInfoWindow();

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
                //String title = marker.getTitle();
                //String snippet = marker.getSnippet();
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                if (url.equals("없음")) {
                    intent.putExtra("query", address);
                } else {
                    intent.putExtra("query", url);
                }
                startActivity(intent);
            }
        });
        /*********************** Map Marker Click ***********************/
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return status;
            }
        });
    }

    private void displayLocationInfo(Location location) {
        StringBuffer sb = new StringBuffer();
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.KOREA);
            List<Address> addressList = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lng), 1);
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
            if(count == 0){
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                LatLng position = new LatLng(latitude, longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17));

                arrayPoints = new ArrayList<LatLng>();
                mMap.addMarker(
                        new MarkerOptions().position(position).title("내 위치"))
                        .showInfoWindow();


                PolylineOptions line = new PolylineOptions()
                        .color(Color.RED)
                        .width(5);

                startLatLng = new LatLng(latitude, longitude);
                endLatLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                double instance = CalculationByDistance(startLatLng, endLatLng);
                instance = Math.round(instance * 1000d)/1000d;
                mapTV.setText(address + "\n거리 : " + instance * 1000 + "m");
                drawPath();
                count ++;
            }
        }

        public double CalculationByDistance(LatLng StartP, LatLng EndP) {
            int Radius = 6371;// radius of earth in Km
            double lat1 = StartP.latitude;
            double lat2 = EndP.latitude;
            double lon1 = StartP.longitude;
            double lon2 = EndP.longitude;
            double dLat = Math.toRadians(lat2 - lat1);
            double dLon = Math.toRadians(lon2 - lon1);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                    + Math.cos(Math.toRadians(lat1))
                    * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                    * Math.sin(dLon / 2);
            double c = 2 * Math.asin(Math.sqrt(a));
            double valueResult = Radius * c;
            double km = valueResult / 1;
            DecimalFormat newFormat = new DecimalFormat("####");
            int kmInDec = Integer.valueOf(newFormat.format(km));
            double meter = valueResult % 1000;
            int meterInDec = Integer.valueOf(newFormat.format(meter));

            return Radius * c;
        }

        private void drawPath(){        //polyline을 그려주는 메소드
            PolylineOptions options = new PolylineOptions()
                    .add(startLatLng)
                    .add(endLatLng)
                    .width(15)
                    .color(Color.RED);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 16));
            mMap.addPolyline(options);
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
