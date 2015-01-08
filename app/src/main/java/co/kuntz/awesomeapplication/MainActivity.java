package co.kuntz.awesomeapplication;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends ActionBarActivity {
    public static final String TAG = MainActivity.class.getName();
    public static final String PREFS_FILE = MainActivity.class.getCanonicalName() + ".preferences";

    private int[] colors = new int[3];

    private AsyncTask<View, Void, Void> colorChangingTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Starting application");
        changeBackgroundColor();

        getColorsFromSharedPreferences();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d(TAG, "Touch event changing hello world text");

            TextView helloText = (TextView) getWindow().getDecorView().findViewById(R.id.hello_world);

            String[] hellos = getResources().getStringArray(R.array.hellos);
            helloText.setText(hellos[new Random().nextInt(hellos.length)] + "!");
        }

        return super.onTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getColorsFromSharedPreferences();

        View view = getWindow().getDecorView().findViewById(R.id.main_layout);
        colorChangingTask = gimmeBackgroundTask();
        colorChangingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, view);

        TextView helloText = (TextView) getWindow().getDecorView().findViewById(R.id.hello_world);

        String[] hellos = getResources().getStringArray(R.array.hellos);
        helloText.setText(hellos[new Random().nextInt(hellos.length)] + "!");
    }

    @Override
    protected void onPause() {
        super.onPause();
        colorChangingTask.cancel(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        colorChangingTask.cancel(true);

        saveColorToSharedPreferences();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        colorChangingTask.cancel(true);
    }

    public AsyncTask<View, Void, Void> gimmeBackgroundTask() {
        return new AsyncTask<View, Void, Void>() {
            protected Void doInBackground(View... views) {
                while (true) {
                    Log.d(TAG, "Changing background color");

                    final View view = views[0];
                    final int initialColor = Color.rgb(colors[0], colors[1], colors[2]);
                    Random random = new Random();

                    for (int i = 0; i < 3; i++) {
                        colors[i] = random.nextInt(256);
                    }

                    final int endColor = Color.rgb(colors[0], colors[1], colors[2]);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ObjectAnimator animator = ObjectAnimator.ofObject(view, "backgroundColor", new ArgbEvaluator(), initialColor, endColor);
                            animator.setDuration(5000);
                            animator.start();
                            //view.setBackgroundColor(Color.rgb(colors[0], colors[1], colors[2]));
                        }
                    });

                    saveColorToSharedPreferences();

                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        //TextView text = (TextView) getWindow().getDecorView().findViewById(R.id.text);
                        //text.setText("Something terrible happened!");
                        Log.e(TAG, "Error changing text", e);
                        break;
                    }
                }
                return null;
            }
        };
    }

    private void changeBackgroundColor() {
        View view = getWindow().getDecorView().findViewById(R.id.main_layout);
        Random random = new Random();

        view.setBackgroundColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
    }

    private void getColorsFromSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_FILE, 0);

        colors[0] = prefs.getInt("red", 145);
        colors[1] = prefs.getInt("green", 243);
        colors[2] = prefs.getInt("blue", 214);
    }

    private void saveColorToSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_FILE, 0);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt("red", colors[0]);
        editor.putInt("green", colors[1]);
        editor.putInt("blue", colors[2]);

        editor.commit();
    }
}
