package com.attendify.app.utils

import android.content.Context
import android.content.ContextWrapper
import androidx.fragment.app.FragmentActivity

/**
 * Finds the FragmentActivity from the context, which is required for BiometricPrompt.
 */
fun Context.findActivity(): FragmentActivity {
    var context = this
    while (context is ContextWrapper) {
        if (context is FragmentActivity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Permissions should be called in the context of an Activity.")
}
