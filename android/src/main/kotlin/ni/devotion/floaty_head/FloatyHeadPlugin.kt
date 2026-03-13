package ni.devotion.floaty_head

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.Settings
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.FlutterEngineGroup
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.FlutterInjector
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BasicMessageChannel
import io.flutter.plugin.common.JSONMessageCodec
import io.flutter.plugin.common.PluginRegistry
import ni.devotion.floaty_head.generated.ChatHeadConfig
import ni.devotion.floaty_head.generated.FloatyHostApi
import ni.devotion.floaty_head.services.FloatyContentJobService
import ni.devotion.floaty_head.utils.Constants
import ni.devotion.floaty_head.utils.Managment
import java.io.IOException

class FloatyHeadPlugin :
    FlutterPlugin,
    ActivityAware,
    FloatyHostApi,
    PluginRegistry.ActivityResultListener {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 2084
        var isServiceRunning = false
    }

    private var activity: Activity? = null
    private var context: Context? = null
    private var mainMessenger: BasicMessageChannel<Any?>? = null
    private var pendingPermissionResult: ((Result<Boolean>) -> Unit)? = null
    private var flutterPluginBinding: FlutterPlugin.FlutterPluginBinding? = null

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        flutterPluginBinding = binding
        context = binding.applicationContext
        FloatyHostApi.setUp(binding.binaryMessenger, this)

        mainMessenger = BasicMessageChannel(
            binding.binaryMessenger,
            Constants.MESSENGER_TAG,
            JSONMessageCodec.INSTANCE,
        )
        mainMessenger?.setMessageHandler { message, reply ->
            val overlayEngine =
                FlutterEngineCache.getInstance().get(Constants.OVERLAY_ENGINE_CACHE_TAG)
            if (overlayEngine != null) {
                val overlayMessenger = BasicMessageChannel<Any?>(
                    overlayEngine.dartExecutor,
                    Constants.MESSENGER_TAG,
                    JSONMessageCodec.INSTANCE,
                )
                overlayMessenger.send(message, reply)
            } else {
                reply.reply(null)
            }
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        FloatyHostApi.setUp(binding.binaryMessenger, null)
        mainMessenger?.setMessageHandler(null)
        mainMessenger = null
        flutterPluginBinding = null
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        Managment.activity = binding.activity
        Managment.globalContext = binding.activity.applicationContext
        binding.addActivityResultListener(this)
    }

    override fun onDetachedFromActivity() {
        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
        Managment.activity = binding.activity
        binding.addActivityResultListener(this)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        activity = null
    }

    override fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }

    override fun requestPermission(callback: (Result<Boolean>) -> Unit) {
        val currentActivity = activity
        if (currentActivity == null) {
            callback(Result.success(false))
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(currentActivity)) {
                callback(Result.success(true))
                return
            }
            pendingPermissionResult = callback
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${currentActivity.packageName}"),
            )
            currentActivity.startActivityForResult(intent, PERMISSION_REQUEST_CODE)
        } else {
            callback(Result.success(true))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val granted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Settings.canDrawOverlays(context)
            } else {
                true
            }
            pendingPermissionResult?.invoke(Result.success(granted))
            pendingPermissionResult = null
            return true
        }
        return false
    }

    override fun showChatHead(config: ChatHeadConfig) {
        val currentActivity = activity ?: return
        val appContext = currentActivity.applicationContext

        config.chatheadIconAsset?.let { loadAssetBitmap(appContext, it) }
            ?.let { Managment.floatingIcon = it }
        config.closeIconAsset?.let { loadAssetBitmap(appContext, it) }
            ?.let { Managment.closeIcon = it }
        config.closeBackgroundAsset?.let { loadAssetBitmap(appContext, it) }
            ?.let { Managment.backgroundCloseIcon = it }
        config.notificationIconAsset?.let { loadAssetBitmap(appContext, it) }
            ?.let { Managment.notificationIcon = it }
        config.notificationTitle?.let { Managment.notificationTitle = it }

        createOverlayEngine(appContext, config.entryPoint)

        val serviceIntent = Intent(appContext, FloatyContentJobService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appContext.startForegroundService(serviceIntent)
        } else {
            appContext.startService(serviceIntent)
        }
        isServiceRunning = true
    }

    override fun closeChatHead() {
        FloatyContentJobService.instance?.closeWindow(true)
        isServiceRunning = false
        destroyOverlayEngine()
    }

    override fun isChatHeadActive(): Boolean = isServiceRunning

    private fun createOverlayEngine(context: Context, entryPoint: String) {
        if (FlutterEngineCache.getInstance().get(Constants.OVERLAY_ENGINE_CACHE_TAG) != null) {
            return
        }
        val engineGroup = FlutterEngineGroup(context)
        val dartEntrypoint = DartExecutor.DartEntrypoint(
            FlutterInjector.instance().flutterLoader().findAppBundlePath(),
            entryPoint,
        )
        val engine = engineGroup.createAndRunEngine(context, dartEntrypoint)
        FlutterEngineCache.getInstance().put(Constants.OVERLAY_ENGINE_CACHE_TAG, engine)
    }

    private fun destroyOverlayEngine() {
        val engine = FlutterEngineCache.getInstance().get(Constants.OVERLAY_ENGINE_CACHE_TAG)
        if (engine != null) {
            FlutterEngineCache.getInstance().remove(Constants.OVERLAY_ENGINE_CACHE_TAG)
            engine.destroy()
        }
    }

    private fun loadAssetBitmap(context: Context, assetPath: String): android.graphics.Bitmap? {
        return try {
            val flutterLoader = FlutterInjector.instance().flutterLoader()
            val lookupKey = flutterLoader.getLookupKeyForAsset(assetPath)
            val inputStream = context.assets.open(lookupKey)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            null
        }
    }
}
