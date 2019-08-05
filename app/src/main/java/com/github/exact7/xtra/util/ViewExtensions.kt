package com.github.exact7.xtra.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.signature.ObjectKey
import com.crashlytics.android.Crashlytics
import com.github.exact7.xtra.GlideApp

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.visible(value: Boolean) {
    visibility = if (value) View.VISIBLE else View.GONE
}

fun View.toggleVisibility() = if (isVisible) gone() else visible()

@SuppressLint("CheckResult")
fun ImageView.loadImage(url: String?, changes: Boolean = false, circle: Boolean = false, diskCacheStrategy: DiskCacheStrategy = DiskCacheStrategy.AUTOMATIC) {
    val context = context ?: return
    if (context is Activity && ((Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN && context.isDestroyed) || context.isFinishing)) {
        return //not enough on some devices?
    }
    try {
        val request = GlideApp.with(context)
                .load(url)
                .diskCacheStrategy(diskCacheStrategy)
                .transition(DrawableTransitionOptions.withCrossFade())
        if (changes) {
            //update every 5 minutes
            val minutes = System.currentTimeMillis() / 60000L
            val lastMinute = minutes % 10
            val key = if (lastMinute < 5) minutes - lastMinute else minutes - (lastMinute - 5)
            request.signature(ObjectKey(key))
        }
        if (circle) {
            request.circleCrop()
        }
        request.into(this)
    } catch (e: IllegalArgumentException) {
        Crashlytics.logException(e)
    }
}

fun EditText.showKeyboard() {
    requestFocus()
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun View.hideKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(windowToken, 0)
}

val View.isKeyboardShown: Boolean
    get() {
        val rect = Rect()
        getWindowVisibleDisplayFrame(rect)
        val screenHeight = rootView.height

        // rect.bottom is the position above soft keypad or device button.
        // if keypad is shown, the r.bottom is smaller than that before.
        val keypadHeight = screenHeight - rect.bottom
        return keypadHeight > screenHeight * 0.15
    }

fun ImageView.setTint(@ColorRes tint: Int) {
    val color = ContextCompat.getColor(context, tint)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        drawable.setTint(color)
    } else {
        val wrap = DrawableCompat.wrap(drawable)
        DrawableCompat.setTint(wrap, color)
    }
}

fun ImageView.enable() {
    isEnabled = true
    setColorFilter(Color.WHITE)
}

fun ImageView.disable() {
    isEnabled = false
    setColorFilter(Color.GRAY)
}