package com.example.soundsenseapp.data.sensorData;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class Temperature implements SensorEventListener {

    private static final String TAG = "TemperatureSensor";
    private SensorManager sensorManager;
    private Sensor temperatureSensor;
    private TemperatureListener listener;

    public Temperature(Context context, TemperatureListener listener) {
        this.listener = listener;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        }

        if (temperatureSensor == null) {
            Log.e(TAG, "Temperature sensor is unavailable");
            listener.onTemperatureUnavailable();
        }
    }

    public void startListening() {
        if (temperatureSensor != null) {
            sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void stopListening() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            float temperature = event.values[0];
            Log.d(TAG, "Current temperature: " + temperature + "°C");
            listener.onTemperatureChanged(temperature);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public interface TemperatureListener {
        void onTemperatureChanged(float temperature);
        void onTemperatureUnavailable();
    }
}

