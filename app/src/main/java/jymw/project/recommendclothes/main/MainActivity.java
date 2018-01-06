package jymw.project.recommendclothes.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import jymw.project.recommendclothes.R;
import jymw.project.recommendclothes.location.LocationActivity;
import jymw.project.recommendclothes.main.database.DatabaseClass;
import jymw.project.recommendclothes.main.permission.PermissionChecker;
import jymw.project.recommendclothes.main.weather.CoordinatesXYItem;
import jymw.project.recommendclothes.main.weather.ForecastSpaceTask;
import jymw.project.recommendclothes.main.weather.ForecastTimeTask;
import jymw.project.recommendclothes.notification.NotificationActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = getClass().getSimpleName();
    private Context context;
    private DatabaseClass database;
    private LocationManager locationManager;
    private Location location;
    private LocationListener locationListener;
    private Geocoder geocoder;
    public ArrayList<WeatherInfoItem> itemList;
    private ViewPagerAdapter viewPagerAdapter;
    private HorizontalScrollView hScrollView;
    private long pressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate()");
        context = MainActivity.this;
        database = new DatabaseClass(context, "weatherInfo");
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                MainActivity.this.location = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        requestLocationUpdates();
        geocoder = new Geocoder(context);
        initItemList();
        viewPagerAdapter = new ViewPagerAdapter(context, itemList);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        hScrollView = (HorizontalScrollView) findViewById(R.id.hScrollView);
        initViewPager();

        findViewById(R.id.ivSearch).setOnClickListener(this);
        findViewById(R.id.ivDelete).setOnClickListener(this);
        findViewById(R.id.ivSchedule).setOnClickListener(this);
        findViewById(R.id.tvClothes).setOnClickListener(this);
    }

    private void initItemList() {
        itemList = new ArrayList<>();
        int rowCount = database.getRowCount();
        Double latitude;
        Double longitude;
        int id = 1;
        do {
            if (rowCount == 0) {
                latitude = 37.5657037; // 서울시청의 위도
                longitude = 126.9768616; // 서울시청의 경도
                database.insertData(latitude, longitude);
            } else {
                latitude = database.selectData("latitude", id);
                longitude = database.selectData("longitude", id);
            }
            itemList.add(getWeatherInfo(latitude, longitude));
            id++;
        } while (id < rowCount);
    }

    public WeatherInfoItem getWeatherInfo(Double latitude, Double longitude) {
        try {
            JSONObject forecastTimeInfo = getWeatherInfo(latitude, longitude, 1);
            JSONObject forecastSpaceInfo = getWeatherInfo(latitude, longitude, 21);
            String thoroughfare = getThoroughfare(latitude, longitude);
            String weather = (String) forecastTimeInfo.get("SKY");
            String tempMin = (String) forecastSpaceInfo.get("TMN");
            String tempNow = (String) forecastTimeInfo.get("T1H");
            String tempMax = (String) forecastSpaceInfo.get("TMX");
            String evaluation = "평가";
            String wind = (String) forecastTimeInfo.get("WSD");
            String dust = "먼지";
            String humidity = (String) forecastTimeInfo.get("REH");
            return new WeatherInfoItem(thoroughfare, weather, tempMin, tempNow, tempMax, evaluation, wind, dust, humidity);
        } catch (NullPointerException | JSONException e) {
            System.out.println(e.getLocalizedMessage());
            return null;
        }
    }

    private void initViewPager() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e(TAG, "onPageScrolled()");
            }

            @Override
            public void onPageSelected(int position) {
                Log.e(TAG, "onPageSelected()");
                Log.e("포지션", String.valueOf(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.e(TAG, "onPageScrollStateChanged()");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume()");
    }

    private void requestLocationUpdates() {
        new PermissionChecker(context).execute();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Boolean isGPSProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isGPSProvider) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } else {
            int MIN_TIME = 1;
            int MIN_DISTANCE = 100;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivSearch:
                startActivity(new Intent(context, LocationActivity.class));
                break;
            case R.id.ivSchedule:
                startActivity(new Intent(context, NotificationActivity.class));
                break;
            case R.id.ivDelete:
                deleteItem();
                break;
            default:
                break;
        }
    }

    private void deleteItem() {

    }

    private String getThoroughfare(Double latitude, Double longitude) {
        String thoroughfare;
        try {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 10);
            thoroughfare = addressList.get(0).getThoroughfare();
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
            thoroughfare = "";
        }
        return thoroughfare;
    }

    /**
     * 위도, 경도로 날씨 정보를 가져오는 함수
     *
     * @param latitude  위도
     * @param longitude 경도
     * @return JSONObject 날씨 정보
     */
    private JSONObject getWeatherInfo(Double latitude, Double longitude, int operationNumber) {
        JSONObject weatherInfo = null;
        try {
            CoordinatesXYItem item = new CoordinatesXYItem(latitude, longitude);
            if (operationNumber == 1) {
                weatherInfo = new ForecastTimeTask().execute(item.X, item.Y).get();
            } else if (operationNumber == 21 || operationNumber == 22) {
                weatherInfo = new ForecastSpaceTask(operationNumber).execute(item.X, item.Y).get();
            }
        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e.getLocalizedMessage());
        }
        return weatherInfo;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        viewPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (pressedTime <= System.currentTimeMillis() - 2000) {
            pressedTime = System.currentTimeMillis();
            Toast.makeText(context, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            } else {
                finish();
            }
        }
    }

    // Indicator : http://2codelab.tistory.com/135
}