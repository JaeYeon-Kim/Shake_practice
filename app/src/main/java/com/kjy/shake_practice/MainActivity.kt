package com.kjy.shake_practice

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.kjy.shake_practice.databinding.ActivityMainBinding
import render.animations.Attention
import render.animations.Bounce
import render.animations.Render
import kotlin.math.sqrt
class MainActivity : AppCompatActivity(), SensorEventListener {

    val TAG: String = "로그"

    // 센서 매니저 변수 구현
    private lateinit var sensorManger: SensorManager
    // 동작 기본값
    private var accel: Float = 0.0f

    // 현재 이동하는 변수
    private var accelCurrent: Float = 0.0f

    private var accelLast: Float = 0.0f

    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 메인 액티비티 로그
        Log.d(TAG, "MainActivity - onCreate() called")

        // 센서 매니저를 통해 휴대폰의 센서를 가져옴(Sensor Manager로 형변환)
        this.sensorManger = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        accel = 10f
        // 지구의 중력을 가져옴
        accelCurrent = SensorManager.GRAVITY_EARTH
        accelLast = SensorManager.GRAVITY_EARTH
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "MainActivity - onAccuracyChanged() called")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        //Log.d(TAG, "MainActivity - onSensorChanged() called")

        val x: Float = event?.values?.get(0) as Float
        val y: Float = event?.values?.get(1) as Float
        val z: Float = event?.values?.get(2) as Float


        // 텍스트뷰의 텍스트를 바꿔서 값을 넘겨줌.
        binding.xText.text = "X: " + x.toInt().toString()
        binding.yText.text = "Y: " + y.toInt().toString()
        binding.zText.text = "Z: " + z.toInt().toString()

        // 라스트 = 현재
        accelLast = accelCurrent

        // 전체를 Float 형식으로 변경
        accelCurrent = sqrt((x * x+ y * y + z * z).toDouble()).toFloat()

        // 델타값
        val delta: Float = accelCurrent - accelLast

        accel = accel * 0.9f + delta

        // accel 값이 30을 넘었을 때 로그값
        if (accel > 30) {
            Log.d(TAG, "MainActivity - 흔들었음")
            Log.d(TAG, "MainActivity - accel : ${accel}")

            binding.faceImg.setImageResource(R.drawable.veryhappy)

            // render class 생성
            val render = Render(this)

            render.setAnimation(Attention().Wobble(binding.faceImg))
            render.start()

            // 핸들러로 딜레이를 줌
            Handler().postDelayed({
                binding.faceImg.setImageResource(R.drawable.smile)
            }, 1000L)
        }
    }

    override fun onResume() {
        Log.d(TAG, "MainActivity - onResume() called")
        sensorManger.registerListener(this, sensorManger.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
        super.onResume()
    }

    override fun onPause() {
        sensorManger.unregisterListener(this)
        super.onPause()
    }

}