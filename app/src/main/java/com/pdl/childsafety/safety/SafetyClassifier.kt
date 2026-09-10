package com.pdl.childsafety.safety

import android.graphics.Bitmap

data class SafetyResult(
    val highRisk: Boolean,
    val category: RiskCategory? = null,
    val confidence: Float = 0f
)

interface SafetyClassifier : AutoCloseable {
    fun classify(frame: Bitmap): SafetyResult
    override fun close() = Unit
}
