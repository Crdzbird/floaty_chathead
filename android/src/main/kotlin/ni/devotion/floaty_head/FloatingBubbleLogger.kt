package ni.devotion.floaty_head

import android.util.Log

class FloatingBubbleLogger {
    private var isDebugEnabled = false
    private var tag: String
    init {
        tag = FloatingBubbleLogger::class.java.simpleName
    }
    fun setTag(tag: String): FloatingBubbleLogger {
        this.tag = tag
        return this
    }
    fun setDebugEnabled(enabled: Boolean): FloatingBubbleLogger {
        isDebugEnabled = enabled
        return this
    }
    fun log(message: String?) = if (isDebugEnabled) Log.d(tag, message) else null
    fun log(message: String?, throwable: Throwable?) = if (isDebugEnabled) Log.e(tag, message, throwable) else null
}