package com.cs407.lab4_milestone1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {
    private val TAG = "MyActivity"
    private var job: Job? = null
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var toggleSwitch: Switch
    private lateinit var progressText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        startButton = findViewById(R.id.start)
        stopButton = findViewById(R.id.stop)
        toggleSwitch = findViewById(R.id.toggleSwitch)
        progressText = findViewById(R.id.progressText)

        progressText.text = "Download Progress: 0%"

        startButton.setOnClickListener { startDownload(it) }
        stopButton.setOnClickListener { stopDownload(it) }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private suspend fun mockfileDownloader() {
        withContext(Dispatchers.Main) {
            startButton.text = "DOWNLOADING..."
            progressText.text = "Download Progress: 0%"
        }

        for (downloadProgress in 0..100 step 10) {
            Log.d(TAG, "Download Progress $downloadProgress%")
            withContext(Dispatchers.Main) {
                progressText.text = "Download Progress: $downloadProgress%"
            }
            delay(1000)
        }

        withContext(Dispatchers.Main) {
            progressText.text = "Download Complete"
            startButton.text = "START"
            restartActivity()
        }
        delay(2000)
    }

    private fun restartActivity() {
        val intent = intent
        finish()
        startActivity(intent)
    }

    fun startDownload(view: View) {
        if (job == null || job?.isCancelled == true) {
            job = CoroutineScope(Dispatchers.Default).launch { mockfileDownloader() }
        }
    }

    fun stopDownload(view: View) {
        job?.cancel()
        job = null
        runOnUiThread {
            progressText.text = "Download Canceled"
            startButton.text = "START"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }
}
