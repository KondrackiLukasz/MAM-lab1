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
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.lerp

@Composable
fun OrientationAwareBackground(
    zValue: Float,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val t = ((zValue + 9.8f) / 19.6f).coerceIn(0f, 1f)
    val bgColor = lerp(start = Color.Red, stop = Color.Yellow, fraction = t)

    Box(
        modifier = modifier
            .background(bgColor)
            .fillMaxSize()
    ) {
        content()
    }
}

@Composable
fun Compass(azimuth: Float, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Compass", style = MaterialTheme.typography.headlineMedium)
        Canvas(
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            drawCircle(
                color = Color.Gray,
                center = Offset(x = canvasWidth / 2, y = canvasHeight / 2),
                radius = size.minDimension / 2,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )

            rotate(degrees = -azimuth) {
                drawLine(
                    color = Color.Red,
                    start = Offset(x = canvasWidth / 2, y = canvasHeight / 2),
                    end = Offset(x = canvasWidth / 2, y = 0f),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

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
                    OrientationAwareBackground(zValue = zValue) {
                        Column {
                            AccelerometerValues(xValue, yValue, zValue)
                            OrientationValues(azimuth, pitch, roll)
                            Spacer(modifier = Modifier.height(16.dp))
                            Compass(azimuth)
                        }
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
    fun OrientationValues(
        azimuth: Float,
        pitch: Float,
        roll: Float,
        modifier: Modifier = Modifier
    ) {
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
}
