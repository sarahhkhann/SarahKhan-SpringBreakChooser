package com.example.sarahkhan_springbreakchooser

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.net.URLEncoder

class MainActivity : AppCompatActivity(), SensorEventListener {
    private val SPEECH_REQUEST_CODE = 0
    private var selectedLanguage = "en-US"
    private lateinit var sensorManager: SensorManager
    private var shakeDetector: ShakeDetector? = null

    private var accelerometer: Sensor? = null
    private var lastUpdate: Long = 0
    private var last_x: Float = 0.0f
    private var last_y: Float = 0.0f
    private var last_z: Float = 0.0f
    private val SHAKE_THRESHOLD = 800

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val languageRadioGroup: RadioGroup = findViewById(R.id.radioGroup)

        languageRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedLanguage = when (checkedId) {
                R.id.radiorussian -> "ru-RU"
                R.id.radioturkish -> "tr-TR"
                R.id.radiochinese -> "zh-CN"
                R.id.radioitalian -> "it-IT"
                R.id.radioarabic -> "ar-SA"
                R.id.radiohindi -> "hi-IN"
                else -> "en-US" 
            }
            displaySpeechRecognizer(selectedLanguage)
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            shakeDetector = ShakeDetector {
                openGoogleMaps(selectedLanguage)
            }

        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)


        if (accelerometer == null) {
            val text = "Accelerometer not available on this device"
            val duration = Toast.LENGTH_SHORT
            val toast = Toast.makeText(this, text, duration)
            toast.show()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    private fun displaySpeechRecognizer(languageCode: String) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languageCode)
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, languageCode)
        }
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.let { results ->
                val spokenText = results[0]
                val editTextResult: EditText = findViewById(R.id.edittextresult)
                editTextResult.setText(spokenText)
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val curTime = System.currentTimeMillis()
            // only allow one update every 100ms.
            if (curTime - lastUpdate > 100) {
                val diffTime = curTime - lastUpdate
                lastUpdate = curTime

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000

                if (speed > SHAKE_THRESHOLD) {
                    openGoogleMaps(selectedLanguage)
                }

                last_x = x
                last_y = y
                last_z = z
            }
        }
    }
    private fun openGoogleMaps(language: String) {
        Log.d("MainActivity", "Language selected: $selectedLanguage")

        val zoomLevel = 12 // A zoom level of 12 is a good starting point for city views
        val uriString = when (selectedLanguage) {
            "ru-RU" -> "geo:55.7558,37.6173?z=$zoomLevel" // Moscow
            "tr-TR" -> "geo:41.0082,28.9784?z=$zoomLevel" // Istanbul
            "zh-CN" -> "geo:39.9042,116.4074?z=$zoomLevel" // Beijing
            "it-IT" -> "geo:41.9028,12.4964?z=$zoomLevel" // Rome
            "ar-SA" -> "geo:24.7136,46.6753?z=$zoomLevel" // Riyadh
            "hi-IN" -> "geo:28.7041,77.1025?z=$zoomLevel" // New Delhi
            else -> "geo:0,0?q=tourist+attractions&z=$zoomLevel"
        }

        val gmmIntentUri = Uri.parse(uriString)
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        try {
            startActivity(mapIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Google Maps not found", Toast.LENGTH_LONG).show()
        }
    }


}

private fun SensorManager.registerListener(mainActivity: MainActivity, accel: Sensor, sensorDelayNormal: Int) {

}

private fun SensorManager.unregisterListener(mainActivity: MainActivity) {

}
