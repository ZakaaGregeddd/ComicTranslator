package com.example.comictranslator

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.comictranslator.model.LanguageOption
import com.example.comictranslator.service.OverlayService
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_CODE_MEDIA_PROJECTION = 1001
        private const val REQUEST_CODE_OVERLAY_PERMISSION = 1002
        private const val REQUEST_CODE_CAMERA = 1003

        private val SUPPORTED_LANGUAGES = listOf(
            LanguageOption("Auto Detect", "auto", "Automatic"),
            LanguageOption("English", "en", "English"),
            LanguageOption("Indonesian", "id", "Bahasa Indonesia"),
            LanguageOption("Spanish", "es", "Español"),
            LanguageOption("French", "fr", "Français"),
            LanguageOption("German", "de", "Deutsch"),
            LanguageOption("Japanese", "ja", "日本語"),
            LanguageOption("Chinese", "zh", "中文"),
            LanguageOption("Korean", "ko", "한국어"),
            LanguageOption("Portuguese", "pt", "Português"),
            LanguageOption("Russian", "ru", "Русский"),
            LanguageOption("Arabic", "ar", "العربية"),
            LanguageOption("Thai", "th", "ไทย"),
            LanguageOption("Vietnamese", "vi", "Tiếng Việt")
        )
    }

    private lateinit var startButton: MaterialButton
    private lateinit var stopButton: MaterialButton
    private lateinit var sourceLanguageSpinner: Spinner
    private lateinit var targetLanguageSpinner: Spinner
    private lateinit var autoTranslateCheckbox: CheckBox
    private lateinit var detectOrientationCheckbox: CheckBox
    private lateinit var detectBubblesCheckbox: CheckBox
    private lateinit var statusTextView: TextView

    private var mediaProjectionManager: MediaProjectionManager? = null
    private var isTranslating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupLanguageSpinners()
        requestRequiredPermissions()

        startButton.setOnClickListener { startTranslation() }
        stopButton.setOnClickListener { stopTranslation() }

        Log.d(TAG, "MainActivity created")
    }

    private fun initViews() {
        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)
        sourceLanguageSpinner = findViewById(R.id.sourceLanguageSpinner)
        targetLanguageSpinner = findViewById(R.id.targetLanguageSpinner)
        autoTranslateCheckbox = findViewById(R.id.autoTranslateCheckbox)
        detectOrientationCheckbox = findViewById(R.id.detectOrientationCheckbox)
        detectBubblesCheckbox = findViewById(R.id.detectBubblesCheckbox)
        statusTextView = findViewById(R.id.statusTextView)

        mediaProjectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    private fun setupLanguageSpinners() {
        val languageNames = SUPPORTED_LANGUAGES.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languageNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        sourceLanguageSpinner.adapter = adapter
        targetLanguageSpinner.adapter = adapter

        // Set default: Auto Detect untuk source, Indonesian untuk target
        sourceLanguageSpinner.setSelection(0)
        targetLanguageSpinner.setSelection(2)
    }

    private fun requestRequiredPermissions() {
        val permissions = mutableListOf<String>()

        // Check overlay permission
        if (!Settings.canDrawOverlays(this)) {
            showOverlayPermissionDialog()
        }

        // Check camera permission
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(android.Manifest.permission.CAMERA)
        }

        // Check internet permission
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.INTERNET
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(android.Manifest.permission.INTERNET)
        }

        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), REQUEST_CODE_CAMERA)
        }

        Log.d(TAG, "Permission check completed")
    }

    private fun showOverlayPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Overlay Permission Required")
            .setMessage("This app needs permission to display over other apps to show translations")
            .setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun startTranslation() {
        if (isTranslating) return

        // Check overlay permission
        if (!Settings.canDrawOverlays(this)) {
            showOverlayPermissionDialog()
            return
        }

        // Request media projection
        mediaProjectionManager?.createScreenCaptureIntent()?.let {
            startActivityForResult(it, REQUEST_CODE_MEDIA_PROJECTION)
        }
    }

    private fun stopTranslation() {
        if (!isTranslating) return

        isTranslating = false
        startButton.isEnabled = true
        stopButton.isEnabled = false
        statusTextView.text = "Status: Stopped"

        val intent = Intent(this, OverlayService::class.java).apply {
            action = "STOP"
        }
        stopService(intent)

        Log.d(TAG, "Translation stopped")
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_MEDIA_PROJECTION -> {
                if (resultCode == RESULT_OK && data != null) {
                    isTranslating = true
                    startButton.isEnabled = false
                    stopButton.isEnabled = true
                    statusTextView.text = "Status: Translating..."

                    // Get selected languages
                    val sourceLanguage = SUPPORTED_LANGUAGES[sourceLanguageSpinner.selectedItemPosition]
                    val targetLanguage = SUPPORTED_LANGUAGES[targetLanguageSpinner.selectedItemPosition]

                    // Start overlay service
                    val overlayIntent = Intent(this, OverlayService::class.java).apply {
                        action = "START"
                        putExtra("source_language", sourceLanguage.code)
                        putExtra("target_language", targetLanguage.code)
                        putExtra("detect_bubbles", detectBubblesCheckbox.isChecked)
                        putExtra("detect_orientation", detectOrientationCheckbox.isChecked)
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(overlayIntent)
                    } else {
                        @Suppress("DEPRECATION")
                        startService(overlayIntent)
                    }

                    Log.d(TAG, "Translation started: ${sourceLanguage.name} -> ${targetLanguage.name}")
                } else {
                    Log.d(TAG, "Media projection request cancelled")
                    statusTextView.text = "Status: Permission denied"
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CODE_CAMERA -> {
                for ((index, permission) in permissions.withIndex()) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        Log.w(TAG, "Permission denied: $permission")
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isTranslating) {
            stopTranslation()
        }
    }
}
