package com.mmk.aiwritingdetector.util

import android.app.Activity
import java.lang.ref.WeakReference

/**
 * Global provider for current activity context.
 */
object ActivityProvider {
    private var activityRef: WeakReference<Activity>? = null
    
    var currentActivity: Activity?
        get() = activityRef?.get()
        set(value) {
            activityRef = if (value != null) WeakReference(value) else null
        }
}
