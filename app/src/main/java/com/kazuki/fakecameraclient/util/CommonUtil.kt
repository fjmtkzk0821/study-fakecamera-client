package com.kazuki.fakecameraclient.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import kotlin.math.roundToInt

fun padLeft(s: String, n: Int): String {
    return String.format("%${n}s", s)
}

fun Bitmap.scale(factor: Float, filter: Boolean = true): Bitmap {
    val width = (this.width * factor).roundToInt()
    val height = (this.height * factor).roundToInt()
    return Bitmap.createScaledBitmap(this, width, height, filter)
}

fun Modifier.clearFocus(focusManager: FocusManager) = this.pointerInput(Unit) {
    detectTapGestures(onTap = {
        focusManager.clearFocus()
    })
}

fun Color.Companion.parse(str: String): Color {
    return Color(android.graphics.Color.parseColor(str))
}