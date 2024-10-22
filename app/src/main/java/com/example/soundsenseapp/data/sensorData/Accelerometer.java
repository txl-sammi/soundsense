package com.example.soundsenseapp.data.sensorData;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class Accelerometer implements SensorEventListener {

    private static final String TAG = "AccelerometerSensor";
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private AccelerometerListener listener;
    private long lastUpdate = -1;
    private float[] lastVelocity = {0, 0, 0};
    private static final float SMOOTHING_FACTOR = 0.5f;
    private float[] smoothedAcceleration = {0, 0, 0};
    private static final float MOVEMENT_THRESHOLD = 0.1f;

    public Accelerometer(Context context, AccelerometerListener listener) {
        this.listener = listener;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        }

        if (accelerometerSensor == null) {
            Log.e(TAG, "Accelerometer is unavailable");
            listener.onAccelerometerUnavailable();
        }
    }

    public void startListening() {
        if (accelerometerSensor != null) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void stopListening() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            float[] linearAcceleration = event.values;

            for (int i = 0; i < 3; i++) {
                smoothedAcceleration[i] = (SMOOTHING_FACTOR * smoothedAcceleration[i]) + ((1 - SMOOTHING_FACTOR) * linearAcceleration[i]);
            }
            long currentTime = event.timestamp;
            if (lastUpdate != -1) {
                float deltaTime = (currentTime - lastUpdate) * 1.0f / 1000000000.0f;

                if (Math.abs(smoothedAcceleration[0]) > MOVEMENT_THRESHOLD ||
                        Math.abs(smoothedAcceleration[1]) > MOVEMENT_THRESHOLD ||
                        Math.abs(smoothedAcceleration[2]) > MOVEMENT_THRESHOLD) {

                    Log.d(TAG, "Vx: " + linearAcceleration[0]);
                    float velocityX = lastVelocity[0] + smoothedAcceleration[0] * deltaTime;
                    float velocityY = lastVelocity[1] + smoothedAcceleration[1] * deltaTime;
                    float velocityZ = lastVelocity[2] + smoothedAcceleration[2] * deltaTime;

                    lastVelocity[0] = velocityX;
                    lastVelocity[1] = velocityY;
                    lastVelocity[2] = velocityZ;

                    float totalVelocity = (float) Math.sqrt(Math.pow(velocityX, 2) + Math.pow(velocityY, 2) + Math.pow(velocityZ, 2));

                    Log.d(TAG, "Vx: " + lastVelocity[0] + ", Vy: " + lastVelocity[1] + ", Vz: " + lastVelocity[2] + ", Total Velocity: " + totalVelocity);
                    listener.onAccelerometerChanged(totalVelocity);
                }
            }
            lastUpdate = currentTime;
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public interface AccelerometerListener {
        void onAccelerometerChanged(float velocity);
        void onAccelerometerUnavailable();
    }
}
