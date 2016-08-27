package guyana.glass.systers.projectguyana;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.google.android.glass.content.Intents;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardBuilder;
import java.io.*;
import java.util.List;
import android.media.ExifInterface;
import android.widget.TextView;

/**
 * CaptureImage Activity
 * This activity allows to Capture Image through Google Glass's supported Camera Service
 * It can be accessed through Options Menu from Home Screen
 * On Capturing Image it allows to accept Name with the help of Speech Recognizer
 */

public class CaptureImage extends Activity {


    LocationManager mLocationManager;

    private static final int TAKE_PICTURE_REQUEST = 1;
    private GestureDetector mGestureDetector = null;
    private CameraView cameraView;
    private FileObserver observer;

    /**
     * Handling for Speech Recognition
     * Function to initiate Speech Recognizer for accepting Name of file via Speech Input
     * Requires active internet connection
     */

    private static final int SPEECH_REQUEST = 0;
    String spokenText;

    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        startActivityForResult(intent, SPEECH_REQUEST);
    }


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //Initiate Location
        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        // Initiate CameraView
        cameraView = new CameraView(this);
        // Turn on Gestures
        mGestureDetector = createGestureDetector(this);
        setContentView(cameraView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraView != null) {cameraView.releaseCamera();}
        //mCardScroller.activate();
    }

    @Override
    protected void onPause() {
        //mCardScroller.deactivate();
        super.onPause();
        if (cameraView != null) {cameraView.releaseCamera();}
    }

    private GestureDetector createGestureDetector(Context context) {
        GestureDetector gestureDetector = new GestureDetector(context);
        gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                // Make sure view is initiated
                if (cameraView != null) {
                    if (gesture == Gesture.TAP) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (intent != null) {
                            startActivityForResult(intent, TAKE_PICTURE_REQUEST);
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        return gestureDetector;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event)
    {
        if (mGestureDetector != null)
        {return mGestureDetector.onMotionEvent(event);}
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Handle photos
        if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK) {
            String thumbnailPath = data.getStringExtra(com.google.android.glass.content.Intents.EXTRA_THUMBNAIL_FILE_PATH);
            String picturePath = data.getStringExtra(Intents.EXTRA_PICTURE_FILE_PATH);
            processPictureWhenReady(picturePath);
        }
        if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            spokenText = results.get(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void processPictureWhenReady(final String picturePath)
    {
        final File pictureFile = new File(picturePath);
        displaySpeechRecognizer();
        if (pictureFile.exists())
        {
            stopService(this.getIntent());
            //Intent intent = new Intent(getApplicationContext(), Guyana.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //startActivity(intent);
            /*Intent shareIntent = new Intent(this, BluetoothClient.class);
            shareIntent.putExtra(SHARE_PICTURE, picturePath);
            startActivity(shareIntent);
            finish();
            */
        }
        else
        {
            /** ViewGroup vg = (ViewGroup)(cameraView.getParent());
            vg.removeAllViews();
            RelativeLayout layout = new RelativeLayout(this);
            ProgressBar progressBar = new ProgressBar(this,null,android.R.attr.progressBarStyleLarge);
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            layout.addView(progressBar,params);
            setContentView(layout);
            progressBar.setVisibility(View.INVISIBLE); */

            final File parentDirectory = pictureFile.getParentFile();
            observer = new FileObserver(parentDirectory.getPath()) {
                // Protect against additional pending events after CLOSE_WRITE is
                // handled.
                private boolean isFileWritten;
                @Override
                public void onEvent(int event, String path) {
                    if (!isFileWritten) {
                        // For safety, make sure that the file that was created in
                        // the directory is actually the one that we're expecting.
                        File affectedFile = new File(parentDirectory, path);
                        isFileWritten = (event == FileObserver.CLOSE_WRITE && affectedFile.equals(pictureFile));
                        if (isFileWritten) {
                            File videoFile = new File(pictureFile.getParent(),spokenText);
                            if(videoFile!=null){pictureFile.renameTo(videoFile);}
                            //mGestureDetector = createGestureDetector(this);
                            this.stopWatching();
                            processPictureWhenReady(picturePath);

                            /**
                            //Location Service
                            Criteria criteria = new Criteria();
                            criteria.setAccuracy(Criteria.NO_REQUIREMENT); // 1
                            // criteria.setAccuracy(Criteria.ACCURACY_FINE);
                            // criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                            //System.out.println("best provider:" + mLocationManager.getBestProvider(criteria, true)); // 2
                            String allString = "";
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
                                        //System.out.println("getLastKnownLocation for provider " + p + " returns NOT null");
                                        //mTvLocation.setText(location.getLatitude() + ", " + location.getLongitude());
                                        //ExifInterface exif = new ExifInterface(picturePath);
                                        // EXIF Handling
                                        try {
                                            ExifInterface exif = new ExifInterface(picturePath);
                                            double latitude = location.getLatitude();
                                            double longitude = location.getLongitude();
                                            int num1Lat = (int) Math.floor(latitude);
                                            int num2Lat = (int) Math.floor((latitude - num1Lat) * 60);
                                            double num3Lat = (latitude - ((double) num1Lat + ((double) num2Lat / 60))) * 3600000;
                                            int num1Lon = (int) Math.floor(longitude);
                                            int num2Lon = (int) Math.floor((longitude - num1Lon) * 60);
                                            double num3Lon = (longitude - ((double) num1Lon + ((double) num2Lon / 60))) * 3600000;
                                            String lat = num1Lat + "/1," + num2Lat + "/1," + num3Lat + "/1000";
                                            String lon = num1Lon + "/1," + num2Lon + "/1," + num3Lon + "/1000";
                                            if (latitude > 0) {
                                                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N");
                                            } else {
                                                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "S");
                                            }
                                            if (longitude > 0) {
                                                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");
                                            } else {
                                                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "W");
                                            }
                                            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, lat);
                                            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, lon);
                                            exif.saveAttributes();
                                        }

                                    }
                                    catch(IOException e){
                                        e.printstacktrace();
                                    }
                                }
                                else allString += "N;";
                            } */


                            setResult(Activity.RESULT_OK,getIntent());
                            finish();
                            //Go back to home after capturing image
                            // Now that the file is ready, recursively call
                            // processPictureWhenReady again (on the UI thread).
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });
                        }
                    }
                }
            };
           // observer.startWatching();
        }// The file does not exist yet. Before starting the file observer, you
        // can update your UI to let the user know that the application is
        // waiting for the picture (for example, by displaying the thumbnail
        // image and a progress indicator).
    }
    private View buildView() {
        CardBuilder card = new CardBuilder(this, CardBuilder.Layout.TEXT);
        card.setText("Camera Error! Please restart");
        return card.getView();
    }
}
