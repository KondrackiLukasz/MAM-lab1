package com.example.myapplication

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private var xValue by mutableStateOf(0f)
    private var yValue by mutableStateOf(0f)
    private var zValue by mutableStateOf(0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AccelerometerValues(xValue, yValue, zValue)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            xValue = event.values[0]
            yValue = event.values[1]
            zValue = event.values[2]
        }
    }
}

@Composable
fun AccelerometerValues(x: Float, y: Float, z: Float, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = "Accelerometer Values", style = MaterialTheme.typography.headlineMedium)
        Text(text = "X: $x", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Y: $y", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Z: $z", style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun AccelerometerValuesPreview() {
    MyApplicationTheme {
        AccelerometerValues(0f, 0f, 0f)
    }
}
