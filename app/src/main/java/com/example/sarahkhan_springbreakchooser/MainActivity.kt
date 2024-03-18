package com.example.sarahkhan_springbreakchooser

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private val SPEECH_REQUEST_CODE = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val languageRadioGroup: RadioGroup = findViewById(R.id.radioGroup)

        languageRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radiospanish -> displaySpeechRecognizer()
                R.id.radiofrench -> displaySpeechRecognizer()
                R.id.radiochinese -> displaySpeechRecognizer()
                R.id.radioitalian -> displaySpeechRecognizer()
                R.id.radioarabic -> displaySpeechRecognizer()
                R.id.radiohindi -> displaySpeechRecognizer()
            }
        }
    }
    @Deprecated("Deprecated in Java")

    private fun displaySpeechRecognizer() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        }
        startActivityForResult(intent, SPEECH_REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.let { results ->
                val spokenText = results[0]
                val editTextResult: EditText = findViewById(R.id.edittextresult)
                editTextResult.setText(spokenText)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }




}