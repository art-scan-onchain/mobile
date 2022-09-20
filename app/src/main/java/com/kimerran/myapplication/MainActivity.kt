package com.kimerran.myapplication

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*


private const val CAMERA_REQUEST_CODE = 101

class MainActivity : AppCompatActivity() {
    private lateinit var  codeScanner: CodeScanner
    private lateinit var scanner_view: CodeScannerView
    private lateinit var tv_textView: TextView
    private lateinit var web_view: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scanner_view = findViewById<CodeScannerView>(R.id.scanner_view)
        tv_textView = findViewById(R.id.tv_textView)
        web_view = findViewById(R.id.web_view)
        web_view.settings.javaScriptEnabled = true
        web_view.settings.domStorageEnabled = true

        setupPermissions()
        codeScanner()

//        web_view.loadUrl("https://moseo-web.netlify.app/view/uuid-1")
//        web_view.loadUrl("https://example.com")

    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun codeScanner() {
        codeScanner = CodeScanner(this, scanner_view)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode= ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                runOnUiThread {
                    tv_textView.text = it.text
                    web_view.loadUrl("https://moseo-web.netlify.app/view/${it.text}")
                }
            }

            errorCallback = ErrorCallback {
                runOnUiThread {
                    Log.e("Main", "Camera init error: ${it.message}")
                }
            }
        }

        scanner_view.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You need camera permission for this app to run", Toast.LENGTH_SHORT).show()
                } else {
                    // success
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                }
            }
        }
    }
}