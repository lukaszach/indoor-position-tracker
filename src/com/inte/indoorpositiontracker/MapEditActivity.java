package com.inte.indoorpositiontracker;

import java.util.HashMap;
import java.util.List;

import android.graphics.PointF;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

public class MapEditActivity extends MapActivity{
    private WifiPointView mPointer;
    private long touchStarted;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mPointer = mMap.createNewWifiPointOnMap(new PointF(-1000, -1000));
    }
    
    @Override
    public void onReceiveWifiScanResults(List<ScanResult> results) {
        if(results.size() > 0) { // need atleast 3 access points
            HashMap<String, Integer> measurements = new HashMap<String, Integer>();
            for (ScanResult result : results) {
                measurements.put(result.BSSID, result.level);
            }
            Fingerprint f = new Fingerprint(measurements);
            f.setLocation(mPointer.getLocation());
            IndoorPositionTracker application = (IndoorPositionTracker) getApplication();
            application.addFingerprint(f);
        }
    }
    
    public boolean onTouch(View v, MotionEvent event) {
        v.onTouchEvent(event);
        
        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                touchStarted = event.getEventTime();
                break;
            case MotionEvent.ACTION_UP:
                if (event.getEventTime() - touchStarted < 150) {
                    PointF location = new PointF(event.getX(), event.getY());
                    mPointer.setLocation(location);
                    refreshMap();
                }
                break;
        }
        
        return true; // indicate event was handled
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_main, menu);    
        menu.add(1, 1, 0, "EXIT EDIT MODE");        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case 1:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
