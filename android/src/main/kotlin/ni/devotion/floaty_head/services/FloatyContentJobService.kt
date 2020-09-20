package ni.devotion.floaty_head.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import android.graphics.PixelFormat
import android.view.WindowManager.LayoutParams
import ni.devotion.floaty_head.FloatyHeadPlugin
import ni.devotion.floaty_head.R
import ni.devotion.floaty_head.floating_chathead.ChatHeads
import ni.devotion.floaty_head.utils.Constants.INTENT_EXTRA_PARAMS_MAP
import ni.devotion.floaty_head.utils.Managment
import java.lang.Exception
import java.util.*


class FloatyContentJobService : Service() {
    companion object {
        var instance: FloatyContentJobService?= null
        val CHANNEL_ID = "ForegroundServiceChannel"
        val NOTIFICATION_ID = 1
        val INTENT_EXTRA_IS_UPDATE_WINDOW = "IsUpdateWindow"
        val INTENT_EXTRA_IS_CLOSE_WINDOW = "IsCloseWindow"
    }
    var windowManager: WindowManager? = null
    var context: Context? = null
    var notification: Notification? = null
    var chatHeads: ChatHeads? = null

    override fun onCreate() {
        instance = this
        createNotificationChannel()
        showNotificationManager()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(null != intent && intent.extras != null) {
            val paramsMap = (intent.getSerializableExtra(INTENT_EXTRA_PARAMS_MAP) as HashMap<String, Any>?)
            assert(paramsMap != null)
            context = this
            val isCloseWindow = intent.getBooleanExtra(INTENT_EXTRA_IS_CLOSE_WINDOW, false)

            //createWindow()
            if(!isCloseWindow){
                val isUpdateWindow = intent.getBooleanExtra(INTENT_EXTRA_IS_CLOSE_WINDOW, false)
                if(isUpdateWindow){
                    //updateWindow()
                }else{
                    createWindow()
                }
            }else{
                closeWindow(true)
            }
        }
        return START_STICKY
    }

    fun closeWindow(isEverythingDone: Boolean){
        try {
            windowManager?.let { wm ->
                chatHeads?.let { ch ->
                    ch.removeAllViews()
                    wm.removeView(ch)
                    chatHeads = null
                }
            }
            windowManager = null
            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
                Managment.activity?.stopService(Intent(Managment.activity?.applicationContext, this@FloatyContentJobService::class.java))
            }else{
                Managment.activity?.startForegroundService(Intent(Managment.activity?.applicationContext, this@FloatyContentJobService::class.java))
            }
        }catch(ex: Exception){
            Log.e("TAG", "View not found")
        }
        if(isEverythingDone) stopSelf()
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            assert(manager != null)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    fun createWindow() {
        setWindowManager()
        val params:WindowManager.LayoutParams
        params = LayoutParams()
        params.width = LayoutParams.MATCH_PARENT
        params.height = LayoutParams.WRAP_CONTENT
        params.format = PixelFormat.TRANSLUCENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
        params.type = LayoutParams.TYPE_APPLICATION_OVERLAY
        params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL or LayoutParams.FLAG_SHOW_WHEN_LOCKED or LayoutParams.FLAG_NOT_FOCUSABLE
        }
        else
        {
        params.type = LayoutParams.TYPE_SYSTEM_ALERT or LayoutParams.TYPE_SYSTEM_OVERLAY
        params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL or LayoutParams.FLAG_NOT_FOCUSABLE
        }
        chatHeads = ChatHeads(this)
        chatHeads?.add()
    }

    fun showNotificationManager() {
        val notificationIntent = Intent(this, FloatyHeadPlugin::class.java)
        val pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0)
        notification = if(Managment.notificationIcon == null) {
            NotificationCompat.Builder(this, "ForegroundServiceChannel")
                    .setContentTitle("${Managment.notificationTitle} is Currently Running")
                    .setSmallIcon(R.drawable.ic_chathead)
                    .setContentIntent(pendingIntent)
                    .build()
        }else{
            NotificationCompat.Builder(this, "ForegroundServiceChannel")
                    .setContentTitle("${Managment.notificationTitle} is Currently Running")
                    .setLargeIcon(Managment.notificationIcon)
                    .setContentIntent(pendingIntent)
                    .build()
        }
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        assert(notificationManager != null)
        notificationManager.cancel(NOTIFICATION_ID)
        super.onDestroy()
    }

    private fun setWindowManager() = windowManager ?: run { windowManager = getSystemService(WINDOW_SERVICE) as WindowManager }
}