package ni.devotion.floaty_head.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.widget.TextView
import androidx.core.app.NotificationCompat
import ni.devotion.floaty_head.FloatingBubbleService
import ni.devotion.floaty_head.FloatyHeadPlugin
import ni.devotion.floaty_head.R
import ni.devotion.floaty_head.floating_chathead.FloatingBubbleConfig


class FloatingService: FloatingBubbleService() {
    private var binder = LocalBinder()

    override fun onCreate() {
        createNotificationChannel()
        val notificationIntent = Intent(this, FloatyHeadPlugin::class.java)
        val pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0)
        val notification = NotificationCompat.Builder(this, "ForegroundServiceChannel")
                .setContentTitle("Floaty Head is Currently Running")
                .setSmallIcon(R.drawable.ic_chathead)
                .setContentIntent(pendingIntent)
                .build()
        startForeground(1, notification)
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                    "ForegroundServiceChannel",
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

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