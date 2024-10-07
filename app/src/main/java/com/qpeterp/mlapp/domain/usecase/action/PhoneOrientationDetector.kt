package com.qpeterp.mlapp.domain.usecase.action

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.qpeterp.mlapp.utils.log
import kotlin.math.abs

class PhoneOrientationDetector(context: Context) : SensorEventListener {
    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!

    var verticalHilt = true

    init {
        // 센서 리스너 등록
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val y = event.values[1] // Y축 가속도 값
//            val z = event.values[2] // Z축 가속도 값

            // Z축의 값이 9.8에 가까우면, 기기가 바닥에 수직으로 누워 있는 상태
            if (abs(y) > 3) {
                verticalHilt = true
                log("핸드폰이 세워져 있음")
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun unregister() {
        // 센서 리스너 해제
        log("센서 리스너 해제!")
        sensorManager.unregisterListener(this)
    }
}