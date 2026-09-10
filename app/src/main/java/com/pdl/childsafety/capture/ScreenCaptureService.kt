package com.pdl.childsafety.capture

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.IBinder
import android.os.Parcelable
import androidx.core.app.NotificationCompat
import com.pdl.childsafety.overlay.BlockingOverlay
import com.pdl.childsafety.safety.PrototypeSafetyClassifier

class ScreenCaptureService : Service() {
    companion object {
        const val EXTRA_RESULT_CODE = "result_code"
        const val EXTRA_RESULT_DATA = "result_data"
        private const val CHANNEL_ID = "screen_safety"
        private const val NOTIFICATION_ID = 11
    }

    private var projection: MediaProjection? = null
    private var reader: ImageReader? = null
    private val classifier = PrototypeSafetyClassifier()
    private lateinit var overlay: BlockingOverlay
    private var lastAnalysisAt = 0L

    override fun onCreate() {
        super.onCreate()
        overlay = BlockingOverlay(this)
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("儿童安全保护运行中")
            .setContentText("正在本机检查屏幕内容")
            .setSmallIcon(android.R.drawable.ic_secure)
            .build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val resultCode = intent?.getIntExtra(EXTRA_RESULT_CODE, Activity.RESULT_CANCELED)
            ?: return START_NOT_STICKY
        val resultData = intent.parcelableIntent(EXTRA_RESULT_DATA) ?: return START_NOT_STICKY

        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        val density = metrics.densityDpi
        reader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)

        val manager = getSystemService(MediaProjectionManager::class.java)
        projection = manager.getMediaProjection(resultCode, resultData)
        projection?.createVirtualDisplay(
            "PDLScreenSafety",
            width,
            height,
            density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            reader?.surface,
            null,
            null
        )

        reader?.setOnImageAvailableListener({ imageReader ->
            val now = System.currentTimeMillis()
            val image = imageReader.acquireLatestImage() ?: return@setOnImageAvailableListener
            try {
                if (now - lastAnalysisAt < 500) return@setOnImageAvailableListener
                lastAnalysisAt = now
                val plane = image.planes[0]
                val buffer = plane.buffer
                val pixelStride = plane.pixelStride
                val rowStride = plane.rowStride
                val rowPadding = rowStride - pixelStride * width
                val bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888)
                bitmap.copyPixelsFromBuffer(buffer)
                val frame = Bitmap.createBitmap(bitmap, 0, 0, width, height)
                bitmap.recycle()

                val result = classifier.classify(frame)
                frame.recycle()
                if (result.highRisk) overlay.show("检测到高风险内容：${result.category}") else overlay.hide()
            } finally {
                image.close()
            }
        }, null)

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        overlay.hide()
        reader?.close()
        projection?.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(NotificationChannel(CHANNEL_ID, "屏幕安全保护", NotificationManager.IMPORTANCE_LOW))
    }

    @Suppress("DEPRECATION")
    private fun Intent.parcelableIntent(key: String): Intent? =
        if (android.os.Build.VERSION.SDK_INT >= 33) getParcelableExtra(key, Intent::class.java)
        else getParcelableExtra<Parcelable>(key) as? Intent
}
