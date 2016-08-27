package guyana.glass.systers.projectguyana;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.speech.*;
import java.util.List;
import android.os.Environment;
import android.net.Uri;
import android.location.*;
//import com.google.android.glass.media.CameraManager;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import guyana.glass.systers.projectguyana.CameraView;
import java.io.File;

/**
 * An {@link Activity} showing a tuggable "Hello World!" card.
 * <p>
 * The main content view is composed of a one-card {@link CardScrollView} that provides tugging
 * feedback to the user when swipe gestures are detected.
 * If your Glassware intends to intercept swipe gestures, you should set the content view directly
 * and use a {@link com.google.android.glass.touchpad.GestureDetector}.
 *
 * @see <a href="https://developers.google.com/glass/develop/gdk/touch">GDK Developer Guide</a>
 */

/**
 * RecordVideo Activity
 * This activity allows to Record Video through Google Glass's supported Camera Service
 * It can be accessed through Options Menu from Home Screen
 * On Recording Video it allows to accept Name with the help of Speech Recognizer
 */

public class RecordVideo extends Activity {
    private static final int TAKE_VIDEO_REQUEST = 2;
    public static final String SHARE_PICTURE = "picture";
    private GestureDetector mGestureDetector = null;
    private CameraView cameraView;
    private FileObserver observer;

    private static final int SPEECH_REQUEST = 0;
    String spokenText;

    // Function to initiate Speech Recognizer for accepting Name of file via Speech Input
    // Requires active internet connection
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        startActivityForResult(intent, SPEECH_REQUEST);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        //super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Initiate CameraView
        cameraView = new CameraView(this);

        // Turn on Gestures
        mGestureDetector = createGestureDetector(this);
        setContentView(cameraView);

    }
    // options menu


    @Override
    protected void onResume() {
        super.onResume();
        if (cameraView != null) {cameraView.releaseCamera();}
        //cameraView.activate();
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
                        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        if (intent != null) {
                            startActivityForResult(intent, TAKE_VIDEO_REQUEST);
                        }
                        return true;
                    }
                }

                return false;
            }
        });

        return gestureDetector;
    }

    /*
	 * Send generic motion events to the gesture detector
	 */
    @Override
    public boolean onGenericMotionEvent(MotionEvent event)
    {
        if (mGestureDetector != null)
        {return mGestureDetector.onMotionEvent(event);}
        return false;
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Handle photos
        if (requestCode == TAKE_VIDEO_REQUEST && resultCode == RESULT_OK) {
            String thumbnailPath = data.getStringExtra(com.google.android.glass.content.Intents.EXTRA_THUMBNAIL_FILE_PATH);
            String videoPath = data.getStringExtra(com.google.android.glass.content.Intents.EXTRA_VIDEO_FILE_PATH);
            processPictureWhenReady(videoPath);
        }
        if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
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
            /*Intent shareIntent = new Intent(this, BluetoothClient.class);
            shareIntent.putExtra(SHARE_PICTURE, picturePath);
            startActivity(shareIntent);
            finish();
            */
        }
        else
        {

        }// The file does not exist yet. Before starting the file observer, you
            // can update your UI to let the user know that the application is
            // waiting for the picture (for example, by displaying the thumbnail
            // image and a progress indicator).
            /*ViewGroup vg = (ViewGroup)(cameraView.getParent());
            vg.removeAllViews();
            RelativeLayout layout = new RelativeLayout(this);
            ProgressBar progressBar = new ProgressBar(this,null,android.R.attr.progressBarStyleLarge);
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            layout.addView(progressBar,params);
            setContentView(layout); */

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

                        isFileWritten = (event == FileObserver.CLOSE_WRITE
                                && affectedFile.equals(pictureFile));

                        if (isFileWritten) {
                            processPictureWhenReady(picturePath);
                            //File videoFile = new File(pictureFile.getParent(),spokenText);
                            //if(videoFile!=null){pictureFile.renameTo(videoFile);}
                            //guyanaView = new Guyana(this);
                            //guyanaView.

                            // Turn on Gestures
                            //mGestureDetector = createGestureDetector(this);

                            //setContentView(guyanaView);
                            stopWatching();


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

            observer.startWatching();
        }

    /**
     * Builds a Glass styled "Hello World!" view using the {@link CardBuilder} class.
     */
    private View buildView() {
        CardBuilder card = new CardBuilder(this, CardBuilder.Layout.TEXT);
        card.setText(R.string.title_activity_shoot_video);
        return card.getView();
    }

}
