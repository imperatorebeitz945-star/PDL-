package com.pdl.childsafety.safety

import android.graphics.Bitmap

data class SafetyResult(
    val highRisk: Boolean,
    val category: String = "normal",
    val confidence: Float = 0f
)

/**
 * Boundary for the on-device child-safety model.
 * A real TFLite/MediaPipe model can replace the prototype implementation
 * without changing the screen-capture or blocking pipeline.
 */
interface SafetyClassifier {
    fun classify(frame: Bitmap): SafetyResult
}

/**
 * Safe-by-default placeholder. It deliberately never blocks until a vetted
 * local model is wired in; this avoids pretending that risk detection works.
 */
class PrototypeSafetyClassifier : SafetyClassifier {
    override fun classify(frame: Bitmap): SafetyResult = SafetyResult(highRisk = false)
}
