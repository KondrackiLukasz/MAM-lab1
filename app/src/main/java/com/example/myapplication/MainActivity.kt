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
    private var rotationVectorSensor: Sensor? = null

    private var xValue by mutableStateOf(0f)
    private var yValue by mutableStateOf(0f)
    private var zValue by mutableStateOf(0f)

    private var azimuth by mutableStateOf(0f)
    private var pitch by mutableStateOf(0f)
    private var roll by mutableStateOf(0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        AccelerometerValues(xValue, yValue, zValue)
                        OrientationValues(azimuth, pitch, roll)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        rotationVectorSensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                xValue = event.values[0]
                yValue = event.values[1]
                zValue = event.values[2]
            }

            Sensor.TYPE_ROTATION_VECTOR -> {
                val rotationMatrix = FloatArray(9)
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                val orientationValues = FloatArray(3)
                SensorManager.getOrientation(rotationMatrix, orientationValues)
                azimuth = Math.toDegrees(orientationValues[0].toDouble()).toFloat()
                pitch = Math.toDegrees(orientationValues[1].toDouble()).toFloat()
                roll = Math.toDegrees(orientationValues[2].toDouble()).toFloat()
            }
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

@Composable
fun OrientationValues(azimuth: Float, pitch: Float, roll: Float, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = "Orientation Values", style = MaterialTheme.typography.headlineMedium)
        Text(text = "Azimuth: $azimuth", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Pitch: $pitch", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Roll: $roll", style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun AccelerometerValuesPreview() {
    MyApplicationTheme {
        Column {
            AccelerometerValues(0f, 0f, 0f)
            OrientationValues(0f, 0f, 0f)
        }
    }
}