package ni.devotion.floaty_head.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.NotificationCompat
import ni.devotion.floaty_head.FloatyHeadPlugin
import ni.devotion.floaty_head.R
import ni.devotion.floaty_head.floating_chathead.ChatHeads


class FloatingService: Service() {
    companion object {
        lateinit var instance: FloatingService
    }
    private var binder = LocalBinder()
    lateinit var windowManager: WindowManager
    var chatHeads: ChatHeads? = null

    override fun onCreate() {
        instance = this
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
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
        chatHeads = ChatHeads(this)
        chatHeads?.add()
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

    override fun onDestroy() {
        super.onDestroy()
        removeAllViews()
        stopSelf()
    }

    fun removeAllViews() {
        windowManager ?: return
        chatHeads?.let {
            windowManager.removeView(chatHeads)
            it.removeAllViews()
            chatHeads = null
        }
    }

    inner class LocalBinder : Binder() {
        fun getService() = this@FloatingService
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, Service.START_STICKY)
    }
}