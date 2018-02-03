package com.itwill.foursquare.json.parsing;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FourSquareListActivity extends AppCompatActivity {
    ListView FourSquareLV;
    ArrayList<FourSquare> fourSquareList;
    FourSquareAdpter fourSquareAdapter;
    HttpURLConnection connection = null;
    ProgressDialog progressDialog;
    FloatingActionButton mapFB;
    double lat;
    double lng;

    public class FourSquareAdpter extends BaseAdapter {

        @Override
        public int getCount() {
            return fourSquareList.size();
        }

        @Override
        public Object getItem(int i) {
            return fourSquareList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View v, ViewGroup viewGroup) {
            View view = getLayoutInflater().inflate(R.layout.four_square_list_child, null);
            TextView nameTV = view.findViewById(R.id.nameTV);
            TextView addressTV = view.findViewById(R.id.addressTV);
            ImageView imageV = view.findViewById(R.id.imageV);
            String name = fourSquareList.get(i).getName().trim();
            String address = fourSquareList.get(i).getAddress().trim();
            Bitmap bitmap = fourSquareList.get(i).getBitmap();
            nameTV.setText(name);
            addressTV.setText(address);
            imageV.setImageBitmap(bitmap);
            return view;
        }
    }

    public void getMyLocation() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        long minTime = 3000;
        float minDistance = 10;

        MyLocationListener myLocationListener = new MyLocationListener();

        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, myLocationListener);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading...");
        progressDialog.show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four_square_list);
        Button showAllB = (Button) findViewById(R.id.showAllB);
        showAllB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), FourSquareMapAllActivity.class);
                intent.putParcelableArrayListExtra("fourSquareList", fourSquareList);
                startActivity(intent);
            }
        });

        fourSquareList = new ArrayList<FourSquare>();
        FourSquareLV = (ListView) findViewById(R.id.FourSquareLV);
        FourSquareLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("view", i + "");
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), FourSquareMapActivity.class);
                FourSquare fourSquare = fourSquareList.get(i);
                intent.putExtra("name", fourSquare.getName());
                intent.putExtra("url", fourSquare.getUrl());
                intent.putExtra("address", fourSquare.getAddress());
                intent.putExtra("lat", fourSquare.getLat());
                intent.putExtra("lng", fourSquare.getLng());
                intent.putExtra("bitmap", fourSquare.getBitmap());
                intent.putExtra("tips", fourSquare.getTips());
                startActivity(intent);
            }
        });

        lat = 37.499597;
        lng = 127.031372;

        mapFB = (FloatingActionButton) findViewById(R.id.mapFB);
        mapFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMyLocation();
                Toast.makeText(getApplicationContext(), lat + "," + lng, Toast.LENGTH_SHORT).show();
                fourSquareAdapter = new FourSquareAdpter();
                FourSquareLV.setAdapter(fourSquareAdapter);
                new Thread() {
                    @Override
                    public void run() {
                        parseJson();
                    }
                }.start();
            }

        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading...");
        progressDialog.show();

        fourSquareAdapter = new FourSquareAdpter();
        FourSquareLV.setAdapter(fourSquareAdapter);
        new Thread() {
            @Override
            public void run() {
                parseJson();
            }
        }.start();
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ArrayList<FourSquare> recvFourSquareList = (ArrayList<FourSquare>) msg.obj;
                    fourSquareList = recvFourSquareList;
                    /*
                    Adapter를 오버라이딩 하지 않는 경우 새로운 데이터 소스를 추가하는 작업을 해야함
                     */

                    fourSquareAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                    break;
            }
        }
    };

    public void parseJson() {
        try {
            String jsonUrlStr = "https://api.foursquare.com/v2/venues/explore?ll=" + lat + "," + lng
                    + "&radius=250&limit=10&oauth_token=45HKGUJ5WESVY2X1GD0BNDOGWABCPCG2GUH5BWBR2NUXZG5L&v=20180117";
            URL jsonUrl = new URL(jsonUrlStr);
            InputStream jsonIn = jsonUrl.openStream();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(jsonIn)
            );
            StringBuffer jsonSB = new StringBuffer();
            while (true) {
                String readLine = br.readLine();
                if (readLine == null) break;
                jsonSB.append(readLine + "\n");
            }
            Log.e("jsonStr", jsonSB.toString());
            String FourSquaresStr = jsonSB.toString();

            JSONObject FourSquareJsonObject = new JSONObject(FourSquaresStr);
            JSONObject FourSquareJsonMetaObject = FourSquareJsonObject.getJSONObject("meta");
            int requestCode = FourSquareJsonMetaObject.getInt("code");
            Log.e("requestCode", requestCode + "");

            if (requestCode == 200) {
                JSONObject FourSquareJsonResponseObject = FourSquareJsonObject.getJSONObject("response");
                JSONArray GroupsArray = FourSquareJsonResponseObject.getJSONArray("groups");
                JSONObject GroupsObject = GroupsArray.getJSONObject(0);
                JSONArray itemsArray = GroupsObject.getJSONArray("items");
                fourSquareList.clear();
                for (int i = 0; i < itemsArray.length(); i++) {
                    try {
                        JSONObject itemsObject = itemsArray.getJSONObject(i);
                        JSONObject venueObject = itemsObject.getJSONObject("venue");
                        //data
                        String name = venueObject.getString("name");
                        String url = "";
                        if (venueObject.isNull("url")) {
                            url = "없음";
                        } else {
                            url = venueObject.getString("url");
                        }

                        JSONObject locationObject = venueObject.getJSONObject("location");
                        String address = locationObject.getString("address");
                        String lat = locationObject.getString("lat");
                        String lng = locationObject.getString("lng");

                        String imageLink = "";

                        JSONArray categoriesArray = venueObject.getJSONArray("categories");
                        JSONObject categoriesObject = categoriesArray.getJSONObject(0);
                        JSONObject iconObject = categoriesObject.getJSONObject("icon");
                        String prefix = iconObject.getString("prefix");
                        String suffix = iconObject.getString("suffix");
                        imageLink = prefix + "64" + suffix;
                        Log.e("image", imageLink);

                        URL urlink = new URL(imageLink);
                        connection = (HttpURLConnection) urlink.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        Bitmap imageBitmap = BitmapFactory.decodeStream(input);

                        JSONArray tipsArray = itemsObject.getJSONArray("tips");
                        JSONObject tipsObject = tipsArray.getJSONObject(0);
                        String tipsTxt = tipsObject.getString("text");

                        Log.e("-----------", i + "번째------------");
                        Log.e("name", name);
                        Log.e("url", url);
                        Log.e("address", address);
                        Log.e("lat", lat);
                        Log.e("lng", lng);
                        Log.e("tips", tipsTxt);

                        fourSquareList.add(new FourSquare(name, url, address, lat, lng, imageBitmap, tipsTxt));
                    } catch (Exception e) {
                        Log.e("error", e.getMessage());
                        e.printStackTrace();
                    } finally {
                        if (connection != null) connection.disconnect();
                    }
                }
                Message message = new Message();
                message.what = 0;
                message.obj = fourSquareList;
                handler.sendMessage(message);

            } else if (requestCode == 400) {

            } else {
                Log.e("error", "잘못된 요청");
            }
        } catch (Exception e) {

        }
    }

    /****************************************/
    public class MyLocationListener implements LocationListener {
        //위치정보 보여주기
        //구글 맵 이동
        int count = 0;
        @Override
        public void onLocationChanged(Location location) {
            if(count == 0){
                lat = location.getLatitude();
                lng = location.getLongitude();
                count ++;
            }
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
