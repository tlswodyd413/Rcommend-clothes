package jymw.project.recommendclothes.location;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import jymw.project.recommendclothes.R;

/**
 * 사용자가 위치를 지정할 수 있는 액티비티
 */
public class LocationActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();
    private RecyclerView rvLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate()");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_location);
        rvLocation = (RecyclerView) findViewById(R.id.rvLocation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy()");
    }
}