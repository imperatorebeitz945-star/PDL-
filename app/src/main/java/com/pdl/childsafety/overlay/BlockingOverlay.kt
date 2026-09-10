package com.pdl.childsafety.overlay

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView

class BlockingOverlay(private val context: Context) {
    private val windowManager = context.getSystemService(WindowManager::class.java)
    private var view: TextView? = null

    fun show(reason: String) {
        if (view != null || !Settings.canDrawOverlays(context)) return

        val blocker = TextView(context).apply {
            text = "内容已被安全保护\n$reason"
            textSize = 24f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.BLACK)
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.OPAQUE
        )
        windowManager.addView(blocker, params)
        view = blocker
    }

    fun hide() {
        view?.let { windowManager.removeView(it) }
        view = null
    }
}
