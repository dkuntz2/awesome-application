package co.kuntz.awesomeapplication;

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

    private AsyncTask<View, Void, Void> backgroundTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Starting application");
        changeBackgroundColor();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d(TAG, "Touch event changing color");
            changeBackgroundColor();
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

        View view = getWindow().getDecorView().findViewById(R.id.hello_world);
        backgroundTask = gimmeBackgroundTask();
        backgroundTask.execute(view);

        changeBackgroundColor();
    }

    @Override
    protected void onPause() {
        super.onPause();
        backgroundTask.cancel(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        backgroundTask.cancel(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        backgroundTask.cancel(true);
    }

    public AsyncTask<View, Void, Void> gimmeBackgroundTask() {
        return new AsyncTask<View, Void, Void>() {
            protected Void doInBackground(View... views) {
                while (true) {
                    Log.d(TAG, "Changing hello text");

                    final TextView helloWorld = (TextView)views[0];

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String[] hellos = getResources().getStringArray(R.array.hellos);
                            final Random random = new Random();

                            helloWorld.setText(hellos[random.nextInt(hellos.length)] + "!");
                        }
                    });


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
        view.setBackgroundColor(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
    }
}
