package ni.devotion.floaty_head

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.widget.FrameLayout
import androidx.annotation.NonNull
import io.flutter.embedding.engine.loader.FlutterLoader
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.Registrar
import io.flutter.view.FlutterCallbackInformation
import io.flutter.view.FlutterMain
import io.flutter.view.FlutterNativeView
import io.flutter.view.FlutterRunArguments
import ni.devotion.floaty_head.services.FloatyContentJobService.Companion.INTENT_EXTRA_IS_UPDATE_WINDOW
import ni.devotion.floaty_head.services.FloatyIconService
import ni.devotion.floaty_head.services.FloatyContentJobService
import ni.devotion.floaty_head.utils.Commons.getMapFromObject
import ni.devotion.floaty_head.utils.Constants.SHARED_PREF_FLOATY_HEAD
import ni.devotion.floaty_head.utils.Constants.CALLBACK_HANDLE_KEY
import ni.devotion.floaty_head.utils.Constants.CODE_CALLBACK_HANDLE_KEY
import ni.devotion.floaty_head.utils.Constants.BACKGROUND_CHANNEL
import ni.devotion.floaty_head.utils.Constants.INTENT_EXTRA_PARAMS_MAP
import ni.devotion.floaty_head.utils.Constants.METHOD_CHANNEL
import ni.devotion.floaty_head.utils.Constants.KEY_BODY
import ni.devotion.floaty_head.utils.Constants.KEY_FOOTER
import ni.devotion.floaty_head.utils.Constants.KEY_HEADER
import ni.devotion.floaty_head.utils.Managment
import ni.devotion.floaty_head.utils.Managment.bodyMap
import ni.devotion.floaty_head.utils.Managment.bodyView
import ni.devotion.floaty_head.utils.Managment.footerMap
import ni.devotion.floaty_head.utils.Managment.footerView
import ni.devotion.floaty_head.utils.Managment.headerView
import ni.devotion.floaty_head.utils.Managment.headersMap
import ni.devotion.floaty_head.utils.Managment.layoutParams
import ni.devotion.floaty_head.utils.Managment.sIsIsolateRunning
import ni.devotion.floaty_head.views.BodyView
import ni.devotion.floaty_head.views.FooterView
import ni.devotion.floaty_head.views.HeaderView
import java.io.IOException
import java.util.ArrayList
import java.util.HashMap
import kotlin.collections.List
import kotlin.collections.Map

class FloatyHeadPlugin : ActivityAware, FlutterPlugin, MethodChannel.MethodCallHandler {
    companion object {
        var mBound: Boolean = false
        lateinit var instance: FloatyHeadPlugin
        var activity: Activity? = null
        var context: Context? = null
        var sBackgroundFlutterView: FlutterNativeView? = null
        private var channel: MethodChannel? = null
        private var backgroundChannel: MethodChannel? = null
    }
    var sPluginRegistrantCallback: PluginRegistry.PluginRegistrantCallback? = null
    private val CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084

    fun setPluginRegistrant(callback: PluginRegistry.PluginRegistrantCallback) {
        Managment.pluginRegistrantC = callback
        sPluginRegistrantCallback = callback
    }

    fun registerWith(pluginRegistrar: Registrar) {
        context = pluginRegistrar.context()
        channel = MethodChannel(pluginRegistrar.messenger(), METHOD_CHANNEL)
        channel?.setMethodCallHandler(FloatyHeadPlugin())
    }

    fun startCallBackHandler(context: Context) {
        var preferences = context.getSharedPreferences(SHARED_PREF_FLOATY_HEAD, 0)
        val callBackHandle: Long = preferences.getLong(CALLBACK_HANDLE_KEY, -1)
        if (callBackHandle != -1L) {
            FlutterMain.ensureInitializationComplete(context, null)
            val mAppBundlePath: String = FlutterMain.findAppBundlePath()
            val flutterCallback: FlutterCallbackInformation = FlutterCallbackInformation.lookupCallbackInformation(callBackHandle)
            sBackgroundFlutterView?.let { sbfv ->
                backgroundChannel ?: run {
                    backgroundChannel = MethodChannel(sbfv, BACKGROUND_CHANNEL)
                }
                Managment.sIsIsolateRunning.set(true)
            } ?: run {
                sBackgroundFlutterView = FlutterNativeView(context, true)
                if(mAppBundlePath != null && !Managment.sIsIsolateRunning.get()) {
                    Managment.pluginRegistrantC ?: run {
                        Log.i("TAG", "Unable to start callBackHandle... as plugin is not registered")
                        return
                    }
                    val args = FlutterRunArguments()
                    args.bundlePath = mAppBundlePath
                    args.entrypoint = flutterCallback.callbackName
                    args.libraryPath = flutterCallback.callbackLibraryPath
                    sBackgroundFlutterView!!.runFromBundle(args)
                    Managment.pluginRegistrantC?.registerWith(sBackgroundFlutterView!!.getPluginRegistry())
                    backgroundChannel = MethodChannel(sBackgroundFlutterView!!, BACKGROUND_CHANNEL)
                    Managment.sIsIsolateRunning.set(true)
                }
                Managment.sIsIsolateRunning.set(true)
            }
        }
    }

    fun invokeCallBack(context: Context, type: String, params: Any) {
        val argumentsList: MutableList<Any> = ArrayList()
        val preferences = activity!!.applicationContext.getSharedPreferences(SHARED_PREF_FLOATY_HEAD, 0)
        val codeCallBackHandle = preferences.getLong(CODE_CALLBACK_HANDLE_KEY, -1)
        if (codeCallBackHandle == -1L) {
            Log.e("TAG", "Back failed, as codeCallBackHandle is null")
        } else {
            argumentsList.clear()
            argumentsList.add(codeCallBackHandle)
            argumentsList.add(type)
            argumentsList.add(params)
            if(Managment.sIsIsolateRunning.get()) {
                backgroundChannel ?: run{
                    backgroundChannel = MethodChannel(sBackgroundFlutterView, BACKGROUND_CHANNEL)
                }
                try {
                    val retries = intArrayOf(2)
                    invokeCallBackToFlutter(backgroundChannel!!, "callBack", argumentsList, retries)
                    //channel!!.invokeMethod("callBack", argumentsList);
                }catch (ex: Exception) {
                    Log.e("TAG", "Exception in invoking callback $ex")
                }
            } else {
                Log.e("TAG", "invokeCallBack failed, as isolate is not running")
            }
        }
    }

    private fun invokeCallBackToFlutter(channel: MethodChannel, method: String, arguments: List<Any>, retries: IntArray) {
        channel.invokeMethod(method, arguments, object : MethodChannel.Result {
            override fun success(o: Any?) {
                Log.i("TAG", "Invoke call back success")
            }

            override fun error(s: String?, s1: String?, o: Any?) {
                Log.e("TAG", "Error $s$s1")
            }

            override fun notImplemented() {
                if (retries[0] > 0) {
                    Log.d("TAG", "Not Implemented method $method. Trying again to check if it works")
                    invokeCallBackToFlutter(channel, method, arguments, retries)
                } else {
                    Log.e("TAG", "Not Implemented method $method")
                }
                retries[0]--
            }
        })
    }

    private fun FloatyHeadPlugin(_context: Context, _activity: Activity, _methodChannel: MethodChannel) {
        activity = _activity
        context = _context
        channel = _methodChannel
        channel?.let { it.setMethodCallHandler(this) }
    }

    override fun onMethodCall(call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "start" -> {
                Managment.globalContext = activity?.applicationContext
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(activity)) {
                    val packageName = activity?.packageName
                    activity?.startActivityForResult(
                            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")),
                            CODE_DRAW_OVER_OTHER_APP_PERMISSION)
                } else {
                    if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                        val subIntent = Intent(activity?.applicationContext, FloatyContentJobService::class.java)
                        subIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        subIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        subIntent.putExtra(INTENT_EXTRA_IS_UPDATE_WINDOW, true)
                        activity?.startService(subIntent)
                        mBound = true
                    } else {
                        val subIntent = Intent(activity?.applicationContext, FloatyContentJobService::class.java)
                        activity?.startForegroundService(subIntent)
                        mBound = true
                    }
                }
            }
            "isOpen" -> result.success(mBound)
            "close" -> {
                if(mBound){
                    FloatyContentJobService.instance!!.closeWindow(true)
                    if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
                        activity?.stopService(Intent(activity?.applicationContext, FloatyContentJobService::class.java))
                    }else{
                        activity?.startForegroundService(Intent(activity?.applicationContext, FloatyContentJobService::class.java))
                    }
                    mBound = false
                }
            }
            "setIcon" -> result.success(setIconFromAsset(call.arguments as String))
            "setBackgroundCloseIcon" -> result.success(setBackgroundCloseIconFromAsset(call.arguments as String))
            "setCloseIcon" -> result.success(setCloseIconFromAsset(call.arguments as String))
            "setNotificationTitle" -> result.success(setNotificationTitle(call.arguments as String))
            "setNotificationIcon" -> result.success(setNotificationIcon(call.arguments as String))
            "setFloatyHeadContent" -> {
                assert((call.arguments != null))
                val updateParams = call.arguments as HashMap<String, Any>
                headersMap = getMapFromObject(updateParams, KEY_HEADER)
                bodyMap = getMapFromObject(updateParams, KEY_BODY)
                footerMap = getMapFromObject(updateParams, KEY_FOOTER)
                layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
                try {
                    headersMap?.let {
                        headerView = HeaderView(activity!!.applicationContext, it).view
                    }
                    bodyMap?.let {
                        bodyView = BodyView(activity!!.applicationContext, it).view
                    }
                    footerMap?.let {
                        footerView = FooterView(activity!!.applicationContext, it).view
                    }
                } catch (except: Exception) {
                    except.printStackTrace()
                }
                result.success(true)
            }
            "registerCallBackHandler" -> {
                try {
                    val arguments = call.arguments as List<*>
                    arguments ?: result.success(false)
                    arguments?.let {
                        val callBackHandle = (it[0]).toString().toLong()
                        val onClickHandle = (it[1]).toString().toLong()
                        val preferences = activity?.applicationContext!!.getSharedPreferences(SHARED_PREF_FLOATY_HEAD, 0)
                        preferences?.edit()?.putLong(CALLBACK_HANDLE_KEY, callBackHandle)!!.commit()
                        preferences?.edit()?.putLong(CODE_CALLBACK_HANDLE_KEY, onClickHandle)!!.commit()
                        startCallBackHandler(activity!!.applicationContext)
                        result.success(true)
                    }
                } catch (ex: Exception) {
                    Log.e("TAG", "Exception in registerOnClickHandler " + ex.toString())
                    result.success(false)
                }
            }
            else -> result.notImplemented()
        }
    }

    private fun setNotificationTitle(title: String):Int {
        var result = -1
        try {
            Managment.notificationTitle = title
            result = 1
        }catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    private fun setNotificationIcon(assetPath: String):Int {
        var result = -1
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val inputStream = activity!!.applicationContext.assets.open("flutter_assets/" + assetPath)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                Managment.notificationIcon = bitmap
                result = 1
            } else {
                val assetLookupKey = FlutterLoader.getInstance().getLookupKeyForAsset(assetPath)
                val assetManager = activity!!.applicationContext.assets
                val assetFileDescriptor = assetManager.openFd(assetLookupKey)
                val inputStream = assetFileDescriptor.createInputStream()
                Managment.notificationIcon = BitmapFactory.decodeStream(inputStream)
                result = 1
            }
        }catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    private fun setBackgroundCloseIconFromAsset(assetPath: String):Int {
        var result = -1
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val inputStream = activity!!.applicationContext.assets.open("flutter_assets/" + assetPath)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                Managment.backgroundCloseIcon = bitmap
                result = 1
            }
            else {
                val assetLookupKey = FlutterLoader.getInstance().getLookupKeyForAsset(assetPath)
                val assetManager = activity!!.applicationContext.assets
                val assetFileDescriptor = assetManager.openFd(assetLookupKey)
                val inputStream = assetFileDescriptor.createInputStream()
                Managment.backgroundCloseIcon = BitmapFactory.decodeStream(inputStream)
                result = 1
            }
        }catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    private fun setCloseIconFromAsset(assetPath: String):Int {
        var result = -1
        try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                val inputStream = activity!!.applicationContext.assets.open("flutter_assets/" + assetPath)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                Managment.closeIcon = bitmap
                result = 1
            }
            else {
                val assetLookupKey = FlutterLoader.getInstance().getLookupKeyForAsset(assetPath)
                val assetManager = activity!!.applicationContext.assets
                val assetFileDescriptor = assetManager.openFd(assetLookupKey)
                val inputStream = assetFileDescriptor.createInputStream()
                Managment.closeIcon = BitmapFactory.decodeStream(inputStream)
                result = 1
            }
        }catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    private fun setIconFromAsset(assetPath: String):Int {
        var result = -1
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val inputStream = activity!!.applicationContext.assets.open("flutter_assets/" + assetPath)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                Managment.floatingIcon = bitmap
                result = 1
            }
            else {
                val assetLookupKey = FlutterLoader.getInstance().getLookupKeyForAsset(assetPath)
                val assetManager = activity!!.applicationContext.assets
                val assetFileDescriptor = assetManager.openFd(assetLookupKey)
                val inputStream = assetFileDescriptor.createInputStream()
                Managment.floatingIcon = BitmapFactory.decodeStream(inputStream)
                result = 1
            }
        }catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel?.setMethodCallHandler(null)
        //release()
    }

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
      channel = MethodChannel(flutterPluginBinding.binaryMessenger, METHOD_CHANNEL)
      channel?.setMethodCallHandler(this)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
      activity = binding.activity
      Managment.activity = binding.activity
      instance = this@FloatyHeadPlugin
  }

  override fun onDetachedFromActivity() {
      //release()
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
      activity = binding.activity
      Managment.activity = binding.activity
      instance = this@FloatyHeadPlugin
  }

  override fun onDetachedFromActivityForConfigChanges() {
      //release()
  }
}
