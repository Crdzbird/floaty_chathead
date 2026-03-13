package ni.devotion.floaty_head.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import io.flutter.embedding.engine.FlutterEngineCache
import ni.devotion.floaty_head.FloatyHeadPlugin
import ni.devotion.floaty_head.R
import ni.devotion.floaty_head.floating_chathead.ChatHeads
import ni.devotion.floaty_head.generated.FloatyOverlayFlutterApi
import ni.devotion.floaty_head.generated.FloatyOverlayHostApi
import ni.devotion.floaty_head.generated.OverlayFlagMessage
import ni.devotion.floaty_head.generated.OverlayPositionMessage
import ni.devotion.floaty_head.utils.Constants
import ni.devotion.floaty_head.utils.Managment

class FloatyContentJobService : Service(), FloatyOverlayHostApi {

    companion object {
        var instance: FloatyContentJobService? = null
    }

    var windowManager: WindowManager? = null
    var chatHeads: ChatHeads? = null
    private var overlayFlutterApi: FloatyOverlayFlutterApi? = null

    override fun onCreate() {
        instance = this
        createNotificationChannel()
        showNotification()

        val engine = FlutterEngineCache.getInstance().get(Constants.OVERLAY_ENGINE_CACHE_TAG)
        if (engine != null) {
            FloatyOverlayHostApi.setUp(engine.dartExecutor, this)
            overlayFlutterApi = FloatyOverlayFlutterApi(engine.dartExecutor)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (chatHeads == null) {
            createWindow()
        }
        return START_STICKY
    }

    fun createWindow() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        chatHeads = ChatHeads(this)
        chatHeads?.add()

        val engine = FlutterEngineCache.getInstance().get(Constants.OVERLAY_ENGINE_CACHE_TAG)
        if (engine != null) {
            chatHeads?.content?.attachEngine(engine)
        }
    }

    fun closeWindow(stopService: Boolean) {
        chatHeads?.content?.detachEngine()
        chatHeads?.let { ch ->
            windowManager?.let {
                ch.removeAllViews()
                it.removeView(ch)
            }
        }
        chatHeads = null
        windowManager = null

        overlayFlutterApi?.onChatHeadClosed { }

        val engine = FlutterEngineCache.getInstance().get(Constants.OVERLAY_ENGINE_CACHE_TAG)
        if (engine != null) {
            FloatyOverlayHostApi.setUp(engine.dartExecutor, null)
        }

        if (stopService) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(Constants.NOTIFICATION_ID)
            stopSelf()
        }
    }

    fun notifyChatHeadTapped() {
        overlayFlutterApi?.onChatHeadTapped { }
    }

    override fun resizeContent(width: Long, height: Long) {
        chatHeads?.content?.let { panel ->
            val params = panel.layoutParams
            if (params != null) {
                params.width = width.toInt()
                params.height = height.toInt()
                panel.layoutParams = params
            }
        }
    }

    override fun updateFlag(flag: OverlayFlagMessage) {
        // Placeholder for future flag updates on the overlay window
    }

    override fun closeOverlay() {
        closeWindow(true)
        FloatyHeadPlugin.isServiceRunning = false
    }

    override fun getOverlayPosition(): OverlayPositionMessage {
        val topChatHead = chatHeads?.topChatHead
        return if (topChatHead != null) {
            OverlayPositionMessage(
                x = topChatHead.springX.currentValue,
                y = topChatHead.springY.currentValue,
            )
        } else {
            OverlayPositionMessage(x = 0.0, y = 0.0)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                "Floaty Chathead Service",
                NotificationManager.IMPORTANCE_LOW,
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun showNotification() {
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val notificationIntent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, pendingIntentFlags,
        )

        val builder = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("${Managment.notificationTitle} is running")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        if (Managment.notificationIcon != null) {
            builder.setLargeIcon(Managment.notificationIcon)
        }

        builder.setSmallIcon(R.drawable.ic_chathead)

        startForeground(Constants.NOTIFICATION_ID, builder.build())
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(Constants.NOTIFICATION_ID)
        instance = null
        super.onDestroy()
    }
}
