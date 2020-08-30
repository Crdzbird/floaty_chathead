package ni.devotion.floaty_head

import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.IBinder
import android.view.Gravity
import android.widget.TextView

class FloatingService: FloatingBubbleService() {
    private var binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService() = this@FloatingService
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return binder
    }

    override fun getConfig(): FloatingBubbleConfig {
        return FloatingBubbleConfig.Builder()
            .bubbleIconDp(92)
            .paddingDp(4)
            .borderRadiusDp(4)
            .physicsEnabled(true)
            .expandableColor(Color.WHITE)
            .triangleColor(Color.WHITE)
            .gravity(Gravity.END)
            .removeBubbleAlpha(0.75f)
            .expandableView(expandableView)
            .build()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val view = getInflaterBubble()!!.inflate(R.layout.floating_notification, null)
        (view.findViewById(R.id.tvTitle) as TextView).text = "message"
        expandableView = view
        return super.onStartCommand(intent, flags, startId)
    }
}