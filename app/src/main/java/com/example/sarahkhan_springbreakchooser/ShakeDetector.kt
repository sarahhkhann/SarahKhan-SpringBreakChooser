package com.example.sarahkhan_springbreakchooser

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class ShakeDetector(private val onShake: () -> Unit): SensorEventListener {
    private var lastTime = 0L
    private var lastX = 0.0f
    private var lastY = 0.0f
    private var lastZ = 0.0f
    private var shakeThreshold = 2.5f
    private var shakeTimeout = 500

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        val currentTime = System.currentTimeMillis()
        if ((currentTime - lastTime) > shakeTimeout) {
            val x = event!!.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val acceleration = sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH
            if (acceleration > shakeThreshold) {
                lastTime = currentTime
                onShake()
            }

            lastX = x
            lastY = y
            lastZ = z
        }
    }
}
