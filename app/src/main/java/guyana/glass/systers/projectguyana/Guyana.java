package guyana.glass.systers.projectguyana;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import android.view.MotionEvent;
import android.view.*;


/**
 * An {@link Activity} showing a tuggable "Hello World!" card.
 * <p/>
 * The main content view is composed of a one-card {@link CardScrollView} that provides tugging
 * feedback to the user when swipe gestures are detected.
 * If your Glassware intends to intercept swipe gestures, you should set the content view directly
 * and use a {@link com.google.android.glass.touchpad.GestureDetector}.
 *
 * @see <a href="https://developers.google.com/glass/develop/gdk/touch">GDK Developer Guide</a>
 */

/**
 * Guyana - Launcher Activity
 * It supports Motion events such as Swipe and Taps through
 * GestureDetector
 * Location Activity has been associated with the SWIPE_RIGHT
 * Options Menu to select between Capturing Image or Video has been associated with TAP
 * Switching between these two options can be done with the help of Scroller
 *
 */
public class Guyana extends Activity {

    private View mView;
    private CardScrollView mCardScroller;
    private GestureDetector mGestureDetector = null;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mGestureDetector = createGestureDetector(this);
        mView = buildView();
        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(new CardScrollAdapter() {
            @Override
            public int getCount() {
                return 1;
            }

            @Override
            public Object getItem(int position) {
                return mView;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return mView;
            }

            @Override
            public int getPosition(Object item) {
                if (mView.equals(item)) {
                    return 0;
                }
                return AdapterView.INVALID_POSITION;
            }
        });

        // Handle the TAP event.
         mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Open Menu for Option Selection
                openOptionsMenu();
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(Sounds.TAP);
            }
        });
        setContentView(mCardScroller);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCardScroller.activate();
    }

    @Override
    protected void onPause() {
        mCardScroller.deactivate();
        super.onPause();
    }

    private GestureDetector createGestureDetector(Context context) {
        GestureDetector gestureDetector = new GestureDetector(context);
        gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                // Plays DISALLOWED sound on TWO_TAP as no action is associated with it
                if (gesture == Gesture.TWO_TAP) {
                    AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    am.playSoundEffect(Sounds.DISALLOWED);
                    return true;
                }
                // Handler for initiating LocationActivity with SWIPE_RIGHT action
                else if (gesture == Gesture.SWIPE_RIGHT) {
                    AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    am.playSoundEffect(Sounds.TAP);
                    Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
                    if (intent != null) {
                        startActivity(intent);
                    }
                    return true;
                }
                // Plays DISALLOWED sound on SWIPE_LEFT as no action is associated with it
                else if (gesture == Gesture.SWIPE_LEFT) {
                    AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    am.playSoundEffect(Sounds.DISALLOWED);
                    return true;
                }
                return false;
            }
        });

        return gestureDetector;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        // Binding GestureDetector on MotionEvent
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }

    private View buildView() {
        CardBuilder card = new CardBuilder(this, CardBuilder.Layout.TEXT);

        card.setText("Welcome");
        return card.getView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.guyana, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;

        switch (item.getItemId()) {
            case R.id.item_take_picture:
                // Handler to start CaptureImage activity on it's selection
                intent = new Intent(getApplicationContext(), CaptureImage.class);
                if (intent != null) {
                    startActivity(intent);
                }
                return true;
            case R.id.item_shoot_video:
                // Handler to start RecordVideo activity on it's selection
                intent = new Intent(getApplicationContext(), RecordVideo.class);
                if (intent != null) {
                    startActivity(intent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
