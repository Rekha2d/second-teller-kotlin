package com.android.rekha.oneminutealert

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.content_activity.*
import java.util.*


class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var time = ""
    private var tts: TextToSpeech? = null
    private var readyToSpeak: Boolean = false
    private var canSpeak: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_activity)
        startTimer()
        initView()
        startTimer()
    }

    private fun initView() {
        speak_button.setOnClickListener {
            if (speak_button.text == getText(R.string.stop))
                stopSpeaking()
            else
                speakTime()
        }
    }

    private fun startTimer() {
        val newTimer = object : CountDownTimer(100000000000, 1000) {
            override fun onFinish() {
            }

            @SuppressLint("NewApi")
            override fun onTick(millisUntilFinished: Long) {
                val c = Calendar.getInstance()
                val hr = c.get(Calendar.HOUR)
                val min = c.get(Calendar.MINUTE)
                val sec = c.get(Calendar.SECOND)
                time = " $hr : $min : $sec "
                timer.text = time
                if (readyToSpeak && canSpeak) {
                    if (sec == 0) speak("$hr , $min") else speak("$sec")

                }
            }
        }
        newTimer.start()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun speak(msg: String) {
        tts!!.speak(msg, TextToSpeech.QUEUE_FLUSH, null, "")
    }
    private fun stopSpeaking(){
        canSpeak = false
        speak_button.text = getText(R.string.start_speaking)
    }

    private fun speakTime() {
        if (tts == null) {
            tts = TextToSpeech(this, this)
        }
        speak_button.text = getText(R.string.stop)
        canSpeak = true
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            speak_button.isEnabled = false
            // set US English as language for tts
            val result = tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language specified is not supported!")
            } else {
                speak_button.isEnabled = true
                readyToSpeak = true
            }

        } else {
            Log.e("TTS", "Initilization Failed!")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tts!!.stop()
        tts!!.shutdown()
    }
}

