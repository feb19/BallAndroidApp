package feb19.jp.ballandroidapp

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SensorEventListener, SurfaceHolder.Callback {

    private var surfaceWidth: Int = 0
    private var surfaceHeight: Int = 0

    private val radius = 50.0f
    private val coef = 1000.0f

    private var ballX = 0.0f
    private var ballY = 0.0f
    private var vx = 0.0f
    private var vy = 0.0f
    private var time = 0L

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            Log.d("MainActivity", "x: ${event.values[0].toString()}, y: ${event.values[1].toString()}, z: ${event.values[2].toString()}")

            val x = -event.values[0]
            val y = event.values[1]

            var t = (System.currentTimeMillis() - time).toFloat()
            time = System.currentTimeMillis()
            t /= 1000.0f

            val dx = vx*t + x*t*t / 2.0f
            val dy = vy*t + y*t*t / 2.0f
            ballX += dx*coef
            ballY += dy*coef
            vx += x*t
            vy += y*t

            if (ballX - radius < 0 && vx < 0) {
                vx = -vx / 1.5f
                ballX = radius
            } else if (ballX + radius > surfaceWidth && vx > 0) {
                vx = -vx / 1.5f
                ballX = surfaceWidth - radius
            }
            if (ballY - radius < 0 && vy < 0) {
                vy = -vy / 1.5f
                ballY = radius
            } else if (ballY + radius > surfaceHeight && vy > 0) {
                vy = -vy / 1.5f
                ballY = surfaceHeight - radius
            }

            draw()
        }
    }

    private fun draw() {
        val canvas = surfaceView.holder.lockCanvas()
        canvas.drawColor(Color.YELLOW)
        canvas.drawCircle(
                ballX,
                ballY,
                radius,
                Paint().apply {
                    color = Color.MAGENTA
                }
        )
        surfaceView.holder.unlockCanvasAndPost(canvas)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val holder = surfaceView.holder
        holder.addCallback(this)

        // lock orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onResume() {
        super.onResume()
//        regist on sufaceholder.callback methods
//        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        val accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
//        sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
//        unregist on sufaceholder.callback methods
//        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        sensorManager.unregisterListener(this)
    }

    override fun surfaceCreated(p0: SurfaceHolder?) {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun surfaceChanged(p0: SurfaceHolder?, format: Int, width: Int, height: Int) {
        surfaceWidth = width
        surfaceHeight = height
        ballX = (width * .5).toFloat()
        ballY = (width * .5).toFloat()
    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.unregisterListener(this)
    }
}
