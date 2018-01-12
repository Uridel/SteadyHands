package com.example.kevin.infrastructureapp;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity  {

    //Database variables


    //Backend variables
    private SensorManager mSensorManager;
    private Sensor mAcceleroMeter;
    private Sensor mProximity;
    private boolean isRunning;
    private float acX, acY, acZ;
    private long pX;
    private Date currentTime ;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;




    //UI variables
    private TextView ProximityResult,  AcceleroResult, Debug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Write a message to the database
        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("LastTime");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager =  (SensorManager) getSystemService(SENSOR_SERVICE);
        mAcceleroMeter = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);


        if(mProximity == null) {
            Log.e("prox", "Proximity sensor not available.");
            finish(); // Close app
        }

        if(mAcceleroMeter == null) {
            Log.e("prox", "Acclerometer sensor not available.");
            finish(); // Close app
        }

        AcceleroResult = (TextView) findViewById(R.id.AcceleroResult);
        ProximityResult = (TextView) findViewById(R.id.ProximityResult);
        Debug =  (TextView) findViewById(R.id.Debug);


    }
    //Using proximity to change colors.
    SensorEventListener proximitySensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Sensor mySensor = sensorEvent.sensor;

            if (mySensor.getType() == Sensor.TYPE_PROXIMITY && isRunning) {
                pX = (long)sensorEvent.values[0];

                if(pX < mProximity.getMaximumRange()) {
                    currentTime = Calendar.getInstance().getTime();

                    getWindow().getDecorView().setBackgroundColor(Color.RED);


                } else {
                    // Nothing is nearby
                    getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                }
                mFirebaseInstance.getReference("LastTime").setValue("A test");

                mFirebaseInstance.getReference("LastTime").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.e("TAG", "LastTime Updated");

                        String appTitle = dataSnapshot.getValue(String.class);

                        // update toolbar title
                        getSupportActionBar().setTitle(appTitle);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.e("TAG", "Failed to read LastTime.", error.toException());
                    }
                });
                //dbRef.child("Proximity").setValue(currentTime);
                ProximityResult.setText(" X " + currentTime);


            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

   /**private void addTime() {
        // time data change listener
        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                // Check for null
                if (user == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }

                Log.e(TAG, "User data is changed!" + user.name + ", " + user.email);

                // Display newly updated name and email
                txtDetails.setText(user.name + ", " + user.email);

                // clear edit text
                inputEmail.setText("");
                inputName.setText("");

                toggleButton();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }**/

    SensorEventListener AccelerometerSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Sensor mySensor = sensorEvent.sensor;

            if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER && isRunning) {
                acX = sensorEvent.values[0];
                acY = sensorEvent.values[1];
                acZ = sensorEvent.values[2];

                AcceleroResult.setText("X" + acX + " Y " + acY + " Z " + acZ);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    //Sensors dont run when app is not in foreground
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(proximitySensorListener);
        mSensorManager.unregisterListener(AccelerometerSensorListener);
        isRunning = false;
    }

    //Turns sensors on when the app is in the foreground
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(proximitySensorListener,
                mProximity, 2 * 1000 * 1000);
        mSensorManager.registerListener(AccelerometerSensorListener, mAcceleroMeter, SensorManager.SENSOR_DELAY_NORMAL);
        isRunning = true;
    }
}




