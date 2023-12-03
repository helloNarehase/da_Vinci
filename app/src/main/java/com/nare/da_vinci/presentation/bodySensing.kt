package com.nare.da_vinci.presentation

import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class bodySensing(activity: Activity): SensorEventListener {
    interface Lit {
        fun onAccChager(Xs:Float, Ys:Float, Zs:Float)
        fun onGyroChager(Xs:Float, Ys:Float, Zs:Float)
        fun onJescher(Motion:String = "Nomal")
    }
    private var mLit:Lit? = null
    private val sensorManager = activity.getSystemService(Activity.SENSOR_SERVICE) as SensorManager
    private val acc: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyro: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val Delay_X = 16*5*1000

    private var count = 0
    private var timestamp: Long = 0
    private var part1 = false
    private var part2 = false
    private var click1 = false
    private var click2 = false
    private var Modes = 0
    private var accs = 0f
    private var rad = 0f
    private var dtime = System.currentTimeMillis()
    private var ta = System.currentTimeMillis()



    fun start(lit:Lit) {
        if(lit == mLit) return
        mLit = lit
        sensorManager.registerListener(this, acc, Delay_X)
        sensorManager.registerListener(this, gyro, Delay_X)
    }
    fun stop() {
        sensorManager.unregisterListener(this)
        mLit = null
    }
    override fun onSensorChanged(p0: SensorEvent?) {
        if(p0!!.sensor.type == Sensor.TYPE_GYROSCOPE)
        {

            mLit?.onGyroChager(rad,p0.values[1], p0.values[1])
            // 디바이스의 각도 계산


            // 디바이스의 각도 계산
//            val angleX = (Math.atan2(p0.values[1].toDouble(), p0.values[2].toDouble()).toFloat() * 180 / Math.PI).toFloat()
//            val angleY = (Math.atan2(p0.values[0].toDouble(), p0.values[2].toDouble()).toFloat() * 180 / Math.PI).toFloat()
//
//            Log.d("MainActivity", "angleX: $angleX, angleY: $angleY")

            val now = System.currentTimeMillis()
//            rad += (p0.values[0] * ((now - dtime)*0.001)).toFloat()
            rad += (p0.values[0] * 0.016).toFloat()
//            Log.e("times", ((now - dtime)*0.001).toString())

            dtime = now


            // 디바이스가 좌우로 2번 흔들리면 제스처가 동작합니다.

            if (p0.values[0] > 7) {
                part1 = true
            }
            if (p0.values[0] < -3 && part1) {
                part2 = true
            }
            if(part1 && part2) {
                count++
                part1 = false
                part2 = false
            }
            if (count == 1 && now - timestamp < 1500) {
                Modes = 0
            } else if (count == 2 && now - timestamp < 1500) {
                Modes = 1
            }
            else if (now - timestamp > 1500) {
                mLit?.onJescher(if(Modes == 0) "Nomal" else if(Modes == 1) "Jet" else "Click")
                timestamp = now
                count = 0
                part1 = false
                part2 = false
            }
        } else if(p0.sensor.type == Sensor.TYPE_ACCELEROMETER)
        {
            mLit?.onAccChager(p0.values[0],p0.values[1], p0.values[2])
            if (p0.values[0] > 5 && p0.values[0] < 10) {
                click1 = true
            }
            if (p0.values[0] < -3 && p0.values[0] > -10 && click1) {
                click2 = true
            }
            if (click1 && click2) {
                click2 = false
                click1 = false
                Modes = 2
                mLit?.onJescher(if(Modes == 0) "Nomal" else if(Modes == 1) "Jet" else "Click")
            }
            accs = p0.values[0]

        }

    }
    override fun onAccuracyChanged(p0: Sensor, p1: Int) {
        // Do something when the accuracy of the sensor changes.
    }
}