package com.pdl.childsafety

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.pdl.childsafety.capture.ScreenCaptureService

class MainActivity : Activity() {
    private val captureRequestCode = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val status = TextView(this).apply {
            text = "儿童屏幕安全原型\n\n第一阶段：屏幕捕获 → 本地风险判断 → 立即遮挡"
            textSize = 20f
        }
        val start = Button(this).apply {
            text = "启动安全保护"
            setOnClickListener { startProtection() }
        }
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 80, 48, 48)
            addView(status)
            addView(start)
        }
        setContentView(layout)
    }

    private fun startProtection() {
        if (!Settings.canDrawOverlays(this)) {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
            return
        }
        val manager = getSystemService(MediaProjectionManager::class.java)
        startActivityForResult(manager.createScreenCaptureIntent(), captureRequestCode)
    }

    @Deprecated("Deprecated in Android API; retained for the minimal MediaProjection prototype.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == captureRequestCode && resultCode == RESULT_OK && data != null) {
            val service = Intent(this, ScreenCaptureService::class.java).apply {
                putExtra(ScreenCaptureService.EXTRA_RESULT_CODE, resultCode)
                putExtra(ScreenCaptureService.EXTRA_RESULT_DATA, data)
            }
            startForegroundService(service)
        }
    }
}
