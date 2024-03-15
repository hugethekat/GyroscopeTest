package com.itson.gyroscopetest

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_GYROSCOPE
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var sensor: Sensor
    private lateinit var textView: TextView
    private lateinit var sharedPreferences: SharedPreferences

    private var lastX: Float = 0.0f
    private var lastY: Float = 0.0f
    private var lastZ: Float = 0.0f

    private var displayedX: Float = 0.0f
    private var displayedY: Float = 0.0f
    private var displayedZ: Float = 0.0f

    private var threshold: Float = 0.0005f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textView)
        sharedPreferences = getSharedPreferences("Giroscopio", Context.MODE_PRIVATE)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(TYPE_GYROSCOPE)!!

        if (sensor == null) {
            textView.text = "No se encontró el giroscopio."
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)
        mostrarUltimosDatos()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        guardarUltimosDatos()
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // no se usa
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        lastX = x
        lastY = y
        lastZ = z

        if (Math.abs(x - displayedX) > threshold || Math.abs(y - displayedY) > threshold || Math.abs(z - displayedZ) > threshold) {
            Log.d("Giroscopio", "Valores actualizados: X=$x, Y=$y, Z=$z")
            displayedX = x
            displayedY = y
            displayedZ = z
            actualizarVista()
        }
    }

    private fun mostrarUltimosDatos() {
        val x = sharedPreferences.getFloat("x", 0.0f)
        val y = sharedPreferences.getFloat("y", 0.0f)
        val z = sharedPreferences.getFloat("z", 0.0f)
    }

    private fun guardarUltimosDatos() {
        sharedPreferences.edit().apply {
            putFloat("x", lastX)
            putFloat("y", lastY)
            putFloat("z", lastZ)
            apply()
        }
    }

    private fun actualizarVista() {
        textView.text = "Giroscopio:\nX: $displayedX\nY: $displayedY\nZ: $displayedZ"
    }
}
