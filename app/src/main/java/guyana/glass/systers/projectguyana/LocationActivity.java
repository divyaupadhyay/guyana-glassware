package guyana.glass.systers.projectguyana;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.location.LocationManager;
import android.location.Location;
import android.location.Criteria;
import android.view.View;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollView;
import java.util.List;
import android.widget.TextView;
import android.location.LocationListener;

/**
 * Location Activity
 * It can be accessed from Launcher Activity through SWIPE_RIGHT
 * It initializes and records current location of Glass by requesting
 * Location from companion Android/iOS device.
 * The Latitude and Longitude are displayed on the screen after fetching from device
 */

public class LocationActivity extends Activity {
    LocationManager mLocationManager;
    Location mLocation;
    TextView mTvLocation;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        mTvLocation = (TextView) findViewById(R.id.tvLocation);
        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    }
    protected void onStart() {
        super.onStart();
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.NO_REQUIREMENT); // 1
        // Defining new criteria for accessing location from Companion Device
        // Other supported criteria for Location are commented below:
        // criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        //System.out.println("best provider:" + mLocationManager.getBestProvider(criteria, true)); // 2
        String allString = "";

        // Enumerating Location from all defined sources
        List<String> providers = mLocationManager.getProviders(criteria, false);
        for (String p : providers) {
            allString += p+":";
            if (mLocationManager.isProviderEnabled(p)) { // 3
                //allString += "Y;";
                //mLocationManager.requestLocationUpdates(p, 10000, 0, this); // 4
                Location location = mLocationManager.getLastKnownLocation(p); // 5
                if (location == null){}
                    //System.out.println("getLastKnownLocation for provider " + p + " returns null");
                else {
                    // Displaying Location fetched on screen
                    //System.out.println("getLastKnownLocation for provider " + p + " returns NOT null");
                    mTvLocation.setText(location.getLatitude() + ", " + location.getLongitude());
                }
            }
            else allString += "N;";
        }
        // on Glass, allString is: remote_gps:Y;remote_network:Y;network:Y;passive:Y
        //mTvLocation.setText(allString);
    }
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    public void onProviderEnabled(String provider) {
    }
    public void onProviderDisabled(String provider) {
    }
    public void onLocationChanged(Location location) {
        mLocation = location;
        mTvLocation.setText(mLocation.getLatitude() + ", " + mLocation.getLongitude());
    }

    /**
     *

    LocationManager mLocationManager;
    Location mLocation;
    private View mView;
    private CardScrollView mCardScroller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        mView = buildView();
        mCardScroller = new CardScrollView(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

    }

    private View buildView() {
        CardBuilder card = new CardBuilder(this, CardBuilder.Layout.TEXT);
        //Location
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.NO_REQUIREMENT); // 1
        // criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        System.out.println("best provider:" + mLocationManager.getBestProvider(criteria, true));
        String allString="";

        List<String> providers = mLocationManager.getProviders(criteria, false);
        for (String p : providers) {
            allString += p + ":";
            if (mLocationManager.isProviderEnabled(p)) { // 3
                allString += "Y;";
                mLocationManager.requestLocationUpdates(p, 10000, 0, this); // 4
                Location location = mLocationManager.getLastKnownLocation(p); // 5
                if (location == null)
                    System.out.println("getLastKnownLocation for provider " + p + " returns null");
                card.setText("No Location");
                else {
                    System.out.println("getLastKnownLocation for provider " + p + " returns NOT null");

                    card.setText(location.getLatitude() + ", " + location.getLongitude());
                }
            } else allString += "N;";
        }

        //card.setText("Welcome");

        return card.getView();
    }
     */
}
