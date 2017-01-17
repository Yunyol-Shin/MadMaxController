package com.example.q.splashcreenexercise;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor type_orientation;

    static boolean forward = false;
    static boolean backward = false;
    static byte tilt = 127;

    static byte connectionNum;
    static boolean unhandled_volume_down = false;
    Socket clientSocket;
    WindowManager windowService;
    int num= 0;
    static TextView connected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        windowService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        type_orientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        int o = getIntent().getIntExtra("id", -1);
        connectionNum = (byte) (o << 4);
        TextView tx = (TextView) findViewById(R.id.textView3);
        tx.setText("id:" + o);
        connected=(TextView) findViewById(R.id.textView4);
        initListeners();


        ImageView cheol = (ImageView) findViewById(R.id.imageView);

        cheol.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    forward = true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    forward = false;
                }


                return true;
            }
        });
        ImageView cheol3 = (ImageView) findViewById(R.id.imageView3);
        cheol3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    backward = true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    backward = false;
                }
                return true;
            }
        });

        Button reconnect=(Button)findViewById(R.id.button);
        reconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if(connected.getText().toString().equals("CONNECTION:OFF")){
                     new Connection().execute("");
                 }
            }
        });
        new Connection().execute("");
    }

    public void initListeners() {
        mSensorManager.registerListener(this, type_orientation, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public class Connection extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
                clientSocket = new Socket("52.78.108.211", 3000);

                OutputStream outToServer = (clientSocket.getOutputStream());
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                byte[] a = new byte[3];
                outToServer.write(a);
                if (connectionNum == -1)
                    return null;
                do {
                    a[2] = connectionNum;
                    if (forward)
                        a[2] = (byte) (a[2] + 1);
                    if (backward)
                        a[2] = (byte) (a[2] + (1 << 1));
                    a[1] = tilt;
                    if (unhandled_volume_down) {
                        a[2] = (byte) (a[2] + (1 << 2));
                        unhandled_volume_down = false;
                    }


                    outToServer.write(a);
                    Thread.sleep(20);

                } while (clientSocket.isConnected());
                outToServer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        protected void onPreExecute(){
            connected.setText("CONNECTION:ON");
        }

        protected void onPostExecute(String result){
            connected.setText("CONNECTION:OFF");
        }
    }

    @Override
    protected void onDestroy() {
        try {
            clientSocket.close();
        } catch (IOException e) {
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ||
                keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
            //Do something
            unhandled_volume_down = true;
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //If type is accelerometer only assign values to global property mGravity

        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {

            float xz_ = event.values[1];
            boolean pos = true;
            if (xz_ < 0)
                pos = false;
            if (!pos)
                xz_ = -xz_;
            if (xz_ < 0)
                xz_ = 0;
            if (xz_ > 70)
                xz_ = 70;
            xz_ = (float) (Math.pow(xz_, 1.5) / Math.pow(70, 1.5) * 127);
            if (!pos)
                xz_ = -xz_;
            if (Surface.ROTATION_270 == windowService.getDefaultDisplay().getRotation())
                xz_ = -xz_;

            xz_ += 128;


            tilt = (byte) (xz_);

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}


