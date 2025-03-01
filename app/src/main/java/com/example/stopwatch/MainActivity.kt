package com.example.stopwatch

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.widget.Button
import android.widget.Chronometer
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var stopwatch: Chronometer // = chronometer
    private lateinit var analogStopwatch: AnalogStopwatchView
    private var running = false                 // = isRunning
    private var offset: Long = 0                // = startTime
    private val updateInterval = 10L // Интервал обновления (10 мс)
    private val handler = Handler(Looper.getMainLooper())

    private val OFFSET_KEY = "offset"
    private val RUNNING_KEY = "running"
    private val BASE_KEY = "base"

    private val updateChronometer = object : Runnable {
        override fun run() {
            if (running) {
                val elapsedTime = SystemClock.elapsedRealtime() - stopwatch.base + offset
                stopwatch.text = formatTime(elapsedTime)
                analogStopwatch.timeMillis = elapsedTime // Обновляем аналоговый секундомер
                handler.postDelayed(this, updateInterval)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        stopwatch = findViewById(R.id.stopwatch)
        analogStopwatch = findViewById(R.id.analogStopwatch)
        stopwatch.text = getString(R.string._00_00_00)

        if (savedInstanceState != null) {
            offset = savedInstanceState.getLong(OFFSET_KEY)
            running = savedInstanceState.getBoolean(RUNNING_KEY)
            if (running) {
                stopwatch.base = savedInstanceState.getLong(BASE_KEY)
                handler.post(updateChronometer)
            } else setBaseTime()
        }

        val startButton = findViewById<Button>(R.id.start_button)
        startButton.setOnClickListener {
            if (!running) {
                setBaseTime()
                handler.post(updateChronometer)
                startButton.setText(R.string.pause)
            } else {
                saveOffset()
                startButton.setText(R.string.resume)
                handler.removeCallbacks(updateChronometer)
            }
            running = !running
        }

        val pauseButton = findViewById<Button>(R.id.pause_button)
        pauseButton.setOnClickListener {
            if (running) {
                saveOffset()
                stopwatch.stop()
                running = false
            }
        }

        val resetButton = findViewById<Button>(R.id.reset_button)
        resetButton.setOnClickListener {
            offset = 0
            setBaseTime()
            if (running) {
                startButton.callOnClick()
            }
            startButton.setText(R.string.start)
            stopwatch.text = getString(R.string._00_00_00)
            analogStopwatch.timeMillis = 0 // Сбрасываем аналоговый секундомер
        }
    }

    //    override fun onStop() {
//        super.onStop()
    override fun onPause() {
        super.onPause()
        if (running) {
            saveOffset()
            stopwatch.stop()
        }
    }

    //    override fun onRestart() {
//        super.onRestart()
    override fun onResume() {
        super.onResume()
        if (running) {
            setBaseTime()
            stopwatch.start()
            offset = 0
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putLong(OFFSET_KEY, offset)
        outState.putBoolean(RUNNING_KEY, running)
        outState.putLong(BASE_KEY, stopwatch.base)
        super.onSaveInstanceState(outState)
    }

    private fun setBaseTime() {
        stopwatch.base = SystemClock.elapsedRealtime() - offset
    }

    private fun saveOffset() {
        offset = SystemClock.elapsedRealtime() - stopwatch.base
    }

    @SuppressLint("DefaultLocale")
    private fun formatTime(elapsedTime: Long): String {
        val minutes = (elapsedTime / 60000) % 60
        val seconds = (elapsedTime / 1000) % 60
        val milliseconds = (elapsedTime % 1000) / 10 // Убираем лишний ноль
        return String.format("%02d:%02d:%02d", minutes, seconds, milliseconds)
    }
}