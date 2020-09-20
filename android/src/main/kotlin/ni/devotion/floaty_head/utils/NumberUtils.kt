package ni.devotion.floaty_head.utils

import android.util.Log

/**
 * Class used for convert any number to [float] or [int] and retrieve any number from an [any] object.
 */
object NumberUtils {
    private const val TAG = "NumberUtils"
    fun getFloat(`object`: Any?) = getNumber(`object`).toFloat()
    fun getInt(`object`: Any?) = getNumber(`object`).toInt()
    private fun getNumber(`object`: Any?): Number {
        var `val`: Number = 0
        if (`object` != null) {
            try {
                `val` = `object` as Number
            } catch (ex: Exception) {
                Log.d(TAG, ex.toString())
            }
        }
        return `val`
    }
}