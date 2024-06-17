package dev.adamag.mobilesecurity1;



import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextInputEditText passwordField;
    private MaterialButton loginButton;
    private MaterialTextView errorMessageField;
    private WifiManager wifiManager;
    private BatteryManager batteryManager;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    private float[] accelerometerData;
    private float[] magneticData;

    private boolean isPointingNorth;
    private boolean isUpsideDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);
        errorMessageField = findViewById(R.id.errorMessageField);
        errorMessageField.setVisibility(View.GONE);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        batteryManager = (BatteryManager) getApplicationContext().getSystemService(Context.BATTERY_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordField.getText().toString().isEmpty() ? "00" : passwordField.getText().toString() ;
                if (checkAllConditions(password)) {
                    startNextActivity();
                }
            }
        });
    }

    private boolean checkAllConditions(String password) {
        StringBuilder errorMessages = new StringBuilder();
        boolean conditionsMet = true;

        if (!wifiManager.isWifiEnabled()) {
            errorMessages.append("WiFi should be turned on\n");
            conditionsMet = false;
        }
        String paddedPassword = String.format("%02d", Integer.parseInt(password));
        int batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        int passwordBatteryLevel = Integer.parseInt(paddedPassword.substring(0, 2));
        if (passwordBatteryLevel != batteryLevel) {
            errorMessages.append("Battery level does not match the password\n");
            conditionsMet = false;
        }

        if (!isPointingNorth) {
            errorMessages.append("Phone should be pointing north\n");
            conditionsMet = false;
        }

        if (!isUpsideDown) {
            errorMessages.append("Phone should be upside down\n");
            conditionsMet = false;
        }

        if (!conditionsMet) {
            errorMessageField.setVisibility(View.VISIBLE);
            errorMessageField.setText("Conditions not met:\n\n" + errorMessages.toString());
        } else {
            errorMessageField.setVisibility(View.GONE);
        }

        return conditionsMet;
    }

    private void startNextActivity() {
        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerData = event.values;
            checkUpsideDownCondition();
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticData = event.values;
        }

        if (accelerometerData != null && magneticData != null) {
            float[] rotationMatrix = new float[9];
            float[] inclinationMatrix = new float[9];
            if (SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, accelerometerData, magneticData)) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(rotationMatrix, orientation);
                float azimuth = orientation[0];
                isPointingNorth = Math.abs(Math.toDegrees(azimuth)) < 10;
            }
        }
    }

    private void checkUpsideDownCondition() {
        if (accelerometerData != null) {
            float zAxis = accelerometerData[2];
            isUpsideDown = zAxis < -9.0;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }
}
