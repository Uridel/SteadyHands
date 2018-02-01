package com.example.kevin.infrastructureapp;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.Console;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


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
    private FirebaseDatabase mFirebaseInstance;
    private SimpleDateFormat dateFormat;
    private String dateTime;
    private DatabaseReference myRefLatestTime, myRefSensor;


    //UI variables
    private TextView ProximityResult,  AcceleroResult, Debug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss ");
        mSensorManager =  (SensorManager) getSystemService(SENSOR_SERVICE);
        mAcceleroMeter = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        myRefLatestTime = mFirebaseInstance.getReference("LatestTime");
        myRefLatestTime.addValueEventListener(valueListener);
        myRefSensor = mFirebaseInstance.getReference("Sensordata");
        myRefSensor.addValueEventListener(sensorListener);

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

       //Be able to show a list of alll sensors present
        Debug =  (TextView) findViewById(R.id.Debug);
        // checkSensors();

    }

    private void checkSensors(){
        // List of Sensors Available
        List<Sensor> msensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        // Print each Sensor available using sSensList as the String to be printed
        String sSensList = new String("");
        Sensor tmp;
        int x,i;
        for (i=0;i<msensorList.size();i++){
            tmp = msensorList.get(i);
            sSensList = " "+sSensList+tmp.getName(); // Add the sensor name to the string of sensors available
        }
        // if there are sensors available show the list
        if (i>0){
            sSensList = getString(R.string.sensors)+":"+sSensList;
            Debug.setText(sSensList);
        }
    }

    // Read from the database
    ValueEventListener valueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            String time = dataSnapshot.getValue(String.class);
            Log.d("eeeeeeeeeeeeeeee",time);
            ProximityResult.setText(time);


        }

        @Override
        public void onCancelled(DatabaseError error) {

        }
    };

    ValueEventListener sensorListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            String accelero = dataSnapshot.child("AcceleroData").getValue(String.class);
            String proximity = dataSnapshot.child("ProximityData").getValue(String.class);
            AcceleroResult.setText(accelero);


        }

        @Override
        public void onCancelled(DatabaseError error) {

        }
    };

    //Using proximity to change colors.
    SensorEventListener proximitySensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Sensor mySensor = sensorEvent.sensor;

            if (mySensor.getType() == Sensor.TYPE_PROXIMITY && isRunning) {
                pX = (long)sensorEvent.values[0];

                if(pX < mProximity.getMaximumRange()) {
                    currentTime = Calendar.getInstance().getTime();

                    dateTime = dateFormat.format(currentTime);

                    getWindow().getDecorView().setBackgroundColor(Color.RED);
                    myRefLatestTime.setValue(dateTime);

                }
                else {
                    // Nothing is nearby
                    getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                    //count time nearby

                    myRefSensor.child("ProximityData").setValue(String.valueOf("pX " + pX));
                }


            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    SensorEventListener AccelerometerSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Sensor mySensor = sensorEvent.sensor;

            if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER && isRunning) {
                acX = sensorEvent.values[0];
                acY = sensorEvent.values[1];
                acZ = sensorEvent.values[2];

             //  AcceleroResult.setText("X" + acX + " Y " + acY + " Z " + acZ);
                myRefSensor.child("AcceleroData").setValue(String.valueOf("X" + acX + " Y " + acY + " Z " + acZ));

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




