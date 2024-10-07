package com.cs407.lab4_milestone1

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

        progressText.text = ""

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
            startButton.text = getString(R.string.download)
            progressText.text = " Download Progress: 0%"
        }

        for (downloadProgress in 0..100 step 10) {
            Log.d(TAG, "Download Progress $downloadProgress%")
            withContext(Dispatchers.Main) {
                progressText.text = "Download Progress: $downloadProgress%"
            }
            delay(1000)
        }

        withContext(Dispatchers.Main) {
            progressText.text = ""
        }
    }

    fun startDownload(view: View) {
        job = CoroutineScope(Dispatchers.Default).launch { mockfileDownloader() }
    }

    fun stopDownload(view: View) {
        job?.cancel()
    }
}
